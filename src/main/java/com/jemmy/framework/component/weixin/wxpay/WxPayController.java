package com.jemmy.framework.component.weixin.wxpay;

import com.github.wxpay.sdk.WXPayUtil;
import com.jemmy.config.WxConfig;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.order.Order;
import com.jemmy.framework.component.order.OrderRepository;
import com.jemmy.framework.component.weixin.wxpay.entity.Refund;
import com.jemmy.framework.component.weixin.wxpay.entity.RefundAmount;
import com.jemmy.framework.component.weixin.wxpay.utils.PayUtils;
import com.jemmy.framework.component.weixin.wxpay.utils.SortUtils;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.RequestResult;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.apache.commons.lang.RandomStringUtils;

import java.util.*;

public class WxPayController<E extends Order, R extends OrderRepository<E>> {

    private final String WX_NOTIFY_URL;

    private final JpaController<E, R> controller;

    private final Request request = new Request();

    public WxPayController(JpaController<E, R> controller, String WX_NOTIFY_URL) {
        this.controller = controller;
        this.WX_NOTIFY_URL = WX_NOTIFY_URL;
    }

    /**
     * 创建支付订单
     *
     * @param order 订单实体
     * @return 支付对象
     */
    public Result<Map<String, String>> createPay(E order) {

        String appId = WxConfig.weAppId;

        String mchKey = WxConfig.mchKey;

        try {
            Result<Map<String, String>> result = PayUtils.createPrepayId(appId, WxConfig.mchId, mchKey, order.getUser().getWxOpenid(), order.getPrice(), order.getUuid(), order.getName(), order.getIp(), WX_NOTIFY_URL, "JSAPI");

            if (result.isBlank()) {
                return result;
            }

            Map<String, String> prepayMap = result.getData();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("appId", appId);
            parameters.put("timeStamp", String.valueOf(new Date().getTime()));
            parameters.put("signType", "MD5");
            parameters.put("nonceStr", RandomStringUtils.randomAlphanumeric(32));
            parameters.put("package", "prepay_id=" + prepayMap.get("prepay_id"));
            StringBuilder sortedParameters = SortUtils.mapByAscii(parameters);
            sortedParameters.append("&key=").append(mchKey);

            String sign = WXPayUtil.MD5(sortedParameters.toString());

            parameters.put("paySign", sign);

            // 写入 Sign 签名
            order.setSign(sign);

            // 更新实体
            controller.controller.onlySave(order);

            return Result.<Map<String, String>>of(ResultCode.HTTP200).setData(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.<Map<String, String>>of(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }

    /**
     * 创建支付订单
     *
     * @param total_fee          总价格
     * @param out_trade_no       订单编号
     * @param body               内容
     * @return 支付对象
     */
    public Result<Map<String, String>> createNativePay(Integer total_fee, String out_trade_no, String body, String ip, String notify_url) {

        String appId = WxConfig.weAppId;

        String mchKey = WxConfig.mchKey;

        try {
            Result<Map<String, String>> result = PayUtils.createPrepayId(appId, WxConfig.mchId, mchKey, null, total_fee, out_trade_no, body, ip, notify_url, "NATIVE");

            if (result.isBlank()) {
                return result;
            }

            return Result.<Map<String, String>>of(ResultCode.HTTP200).setData(result.getData());
        } catch (Exception e) {
            return Result.<Map<String, String>>of(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }

    public Result<Map<String, String>> createJSPay(E order) {
        try {
            Result<Map<String, String>> result = PayUtils.createPrepayId(WxConfig.mpAppId, WxConfig.mchId, WxConfig.mchKey, order.getUser().getMpOpenid(), order.getTotalPrice(), order.getUuid(), order.getName(), order.getIp(), this.WX_NOTIFY_URL, "JSAPI");

            System.out.println(JemmyJson.toJSONString(result));

            if (result.isBlank()) {
                return result;
            }

            Map<String, String> prepayMap = result.getData();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("appId", WxConfig.mpAppId);
            parameters.put("timeStamp", String.valueOf(new Date().getTime()));
            parameters.put("signType", "MD5");
            parameters.put("nonceStr", RandomStringUtils.randomAlphanumeric(32));
            parameters.put("package", "prepay_id=" + prepayMap.get("prepay_id"));
            StringBuilder sortedParameters = SortUtils.mapByAscii(parameters);
            sortedParameters.append("&key=").append(WxConfig.mchKey);

            String sign = WXPayUtil.MD5(sortedParameters.toString());

            parameters.put("paySign", sign);

            // 写入 Sign 签名
            order.setSign(sign);

            // 更新实体
            controller.controller.onlySave(order);

            return Result.<Map<String, String>>of(ResultCode.HTTP200).setData(parameters);
        } catch (Exception e) {
            return Result.<Map<String, String>>of(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }

    /**
     * 创建企业付款订单
     * @return 付款结果
     */
    public static Result<Map<String, String>> createCorporatePayment(String partner_trade_no, String openid, Integer amount, String desc, String ip) {
        String appId = WxConfig.weAppId;

        String mchKey = WxConfig.mchKey;

        try {
            return PayUtils.createCorporatePayment(appId, mchKey, partner_trade_no, openid, amount, desc, ip);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 取消订单
     * @param out_trade_no 订单号
     * @return 取消结果
     */
    public Result<Map<String, String>> closePay(String out_trade_no) {
        String url = "https://api.mch.weixin.qq.com/pay/closeorder";

        Map<String, String> parameters = new TreeMap<>();
        parameters.put("appid", WxConfig.weAppId);
        parameters.put("mch_id", WxConfig.mchId);
        parameters.put("out_trade_no", out_trade_no);
        parameters.put("nonce_str", RandomStringUtils.randomAlphanumeric(32));

        StringBuilder sortedParameters = SortUtils.mapByAscii(parameters);
        sortedParameters.append("&key=").append(WxConfig.mchKey);

        try {
            parameters.put("sign", WXPayUtil.MD5(sortedParameters.toString()));
            return PayUtils.post(url, WXPayUtil.mapToXml(parameters));
        } catch (Exception e) {
            return new Result<Map<String, String>>(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }

    public Result<JemmyJson> refund(Order order, String reason, String notify_url) {
        Refund refund = new Refund();

        // 写入退款订单号
        if (StringUtils.isBlank(order.getRefundNumber())) {
            order.setRefundNumber(UUID.randomUUID().toString());
        }

        RefundAmount amount = new RefundAmount();

        amount.setRefund(order.getPrice());
        amount.setTotal(order.getPrice());

        refund.setOut_trade_no(order.getUuid());
        refund.setOut_refund_no(order.getRefundNumber());
        refund.setReason(reason);
        refund.setNotify_url(notify_url);
        refund.setAmount(amount);

        RequestResult status = request.post("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds", refund);

        System.out.println(status);

//        if (status.isBlank()) {
//            return status;
//        }
//
//        JemmyJson json = status.getData();
//
//        if (json.getString("status").equals("SUCCESS")) {
//
//        }

        return Result.HTTP200();
    }
}
