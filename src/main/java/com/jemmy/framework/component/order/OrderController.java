package com.jemmy.framework.component.order;

import com.github.wxpay.sdk.WXPayUtil;
import com.jemmy.config.WxConfig;
import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.auto.param.AutoParamType;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.user.User;
import com.jemmy.framework.component.weixin.wxpay.WxPayController;
import com.jemmy.framework.component.weixin.wxpay.utils.WXPayStatus;
import com.jemmy.framework.config.Setting;
import com.jemmy.framework.data.sql.Sql;
import com.jemmy.framework.exception.OrderException;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.request.RequestParam;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderController<E extends Order, R extends OrderRepository<E>> extends JpaController<E, R> implements OrderControllerImp<E> {

    private final Sql<E> sql = new Sql<>(this.getEntity());

    private final String table;

    protected final WxPayController<E, R> wxPayController;

    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    /** 订单号前缀 */
    private final String prefix;

    private final OrderChannel channel;

    public OrderController(String prefix, OrderChannel channel) {
        this.prefix = prefix;
        this.channel = channel;

        String notifyUrl = String.format("%s/WebAPI/%s/WxPayCallback", Setting.DOMAIN, this.getEntity().getSimpleName());

        this.wxPayController = new WxPayController<>(this, notifyUrl);

        this.table = EntityUtils.getTableName(this.getEntity());

        EntityAttr entityAttr = this.getEntity().getAnnotation(EntityAttr.class);

        String name = "";

        if (entityAttr != null) {
            name = entityAttr.value();
        }

        if (StringUtils.isBlank(name)) {
            name = this.getEntity().getSimpleName();
        }

        OrderStatisticsController.add(name, this);
    }

    @Post
    public Result<E> create(@AutoParam(type = AutoParamType.JSON) JemmyJson param, @AutoParam User user, HttpServletRequest request) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        E e = getEntity().getConstructor().newInstance();

        this.before(new OrderParam(param), e);

        return this.saveOrder(e, request);
    }

    @Get
    public Result<E> findByUuid(@AutoParam String uuid) {
        return controller.findByUuid(uuid);
    }

    @Post
    public Result<Map<String, String>> createWeiXinPay(@AutoParam E order) {
        return wxPayController.createPay(order);
    }

    @Get
    public Result<Integer> getPaymentStatus(@AutoParam String uuid) {
        Result<E> result = controller.findByUuid(uuid);
        return Result.HTTP200(result.get().getPaymentStatus());
    }

    /**
     * 取消订单
     * @return 状态值
     */
    @Post
    public Result<?> cancel(@AutoParam E order, @AutoParam User user) {
        if (!order.getUser().equals(user)) {
            return Result.HTTP403();
        }

        if (order.getPaymentStatus() != 0) {
            switch (order.getPaymentStatus()) {
                case 1:
                    // 订单已付款
                    return Result.of(ResultCode.HTTP203).setMessage("The order has been paid, and refunds are not currently supported. If you need help, please contact customer service!").setCode("PAID");
                case 2:
                    // 订单已取消
                    return Result.of(ResultCode.HTTP203).setMessage("The order has been cancelled, please contact customer service if you need help!").setCode("CANCELLED");
            }
        }

        order.setPaymentStatus(2);

        return controller.save(order).setData(null);
    }

    /**
     * 删除（隐藏）订单
     * @return 状态值
     */
    @Post
    public Result<?> delete(@AutoParam E order, @AutoParam User user) {
        if (!order.getUser().equals(user)) {
            return Result.HTTP403();
        }

        order.setStatus(2);

        return controller.save(order).setData(null);
    }

    /**
     * 获取所有用户订单
     * @param user 用户
     * @param page 页码
     * @param limit 页数
     * @return 页面实体
     */
    @Get
    public Result<Page<E>> findAllByUserToPage(@AutoParam User user, @AutoParam Integer page, @AutoParam(required = false, defaults = "10") Integer limit) {
        return Result.<Page<E>>HTTP200().setData(repository.findAllByUserAndStatusNotIn(user, PageRequest.of(page, limit, Sort.Direction.DESC, "createdDate"), Collections.singleton(2)));
    }

    /**
     * 微信退款回调
     * @return 状态码
     */
    @Post
    public WXPayStatus wxRefundCallback(@AutoParam(type = AutoParamType.JSON) JemmyJson param) {

        System.out.println(param);

        return WXPayStatus.Success();
    }

    /**
     * 微信支付回调
     * @param request 请求体
     * @return 状态码
     */
    @Post
    public String wxPayCallback(HttpServletRequest request) throws Exception {

        RequestParam requestParam = new RequestParam(request);

        Map<String, String> param = WXPayUtil.xmlToMap(requestParam.getXmlParams());

        if (!param.get("appid").equals(channel.equals(OrderChannel.WX) ? WxConfig.weAppId : WxConfig.mpAppId)) {
            throw OrderException.wxPay("Appid does not match");
        }

        String out_trade_no = param.get("out_trade_no");
        String openid = param.get("openid");

        Result<E> result = findByUuid(out_trade_no);

        if (result.isBlank()) {
            throw OrderException.wxPay(result.getMessage());
        }

        E order = result.getData();

        if (param.get("result_code").equals("SUCCESS")) {
            // 已支付状态
            this.setPaymentStatus(order, 1);
        } else {
            // 支付失败状态
            this.setPaymentStatus(order, 3);

            throw OrderException.wxPay(order, "Sign verification failed");
        }

        // 判断 Sign 校验是否相同
//        if (!order.getSign().equals(param.get("sign"))) {
//            throw OrderException.wxPay(order, "Sign verification failed");
//        }

        // 判断 用户 openid 是否相同
        if (!(channel.equals(OrderChannel.WX) ? order.getUser().getWxOpenid().equals(openid) : order.getUser().getMpOpenid().equals(openid))) {
            throw OrderException.wxPay(order, "Openid does not match");
        }

        // 触发后置事件
        this.after(order);

        // 保存
        controller.save(order);

        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    /**
     * 写入支付状态
     * @param order 订单实体
     * @param status 状态值
     */
    public void setPaymentStatus(E order, Integer status) {
        // 写入支付状态
        order.setPaymentStatus(status);
        // 保存订单实体
        controller.onlySave(order);
    }

    /**
     * 保存订单实体
     * @param order 订单实体
     * @param request 请求实体
     * @return 订单实体
     */
    public Result<E> saveOrder(E order, HttpServletRequest request) {

        // 初始化
        order.setIp(IpUtil.getIpAddr(request));
        order.setPaymentStatus(0);
        order.setOrderNumber(this.getOrderNumber());

        return controller.callbackSave(order);
    }

    /**
     * 获取订单号
     * @return 订单号
     */
    public String getOrderNumber() {
        StringBuilder number = new StringBuilder(prefix);

        // 当前时间
        number.append(formatter.format(new Date()));

        // 4 位随机大写字母
        for (int i = 0; i < 4; i++) {
            number.append((char) (Math.random() * 26 + 'A'));
        }

        return number.toString();
    }

    public List<Double> getChartData() {
        List<?> list = controller.query("select DATE_FORMAT(created_date, \"%Y-%m-%d\"), sum(price) from " + this.table + " where payment_status = 1 and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(created_date) GROUP BY DATE_FORMAT(created_date, \"%Y-%m-%d\")");

        List<Double> dates = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0 ; i < 30; i++, c.add(Calendar.DATE, -1)) {
            String date = sdf.format(c.getTime());

            double q = 0;

            for (Object row : list) {
                Object[] item = (Object[]) row;

                if (item[0].equals(date) && item[1] != null) {

                    Object v = item[1];

                    if (v instanceof Double) {
                        q = ((Double) v) / 100;
                    } else {
                        q = ((BigDecimal) v).doubleValue() / 100;
                    }

                }
            }

            // 从第 0 位添加
            dates.add(0, q);
        }

        return dates;
    }

    public Long getTodayAmount() {
        return statistics.getValueByDayBefore(0);
    }

    public Long getYesterdayAmount() {
        return statistics.getValueByDayBefore(1);
    }

    public Long getTotalAmountByUser(User user) {
        return statistics.getValueByField("price", user.getUuid());
    }
}
