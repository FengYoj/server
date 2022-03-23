package com.jemmy.framework.component.weixin.wxpay.utils;

import com.github.wxpay.sdk.WXPayUtil;
import com.jemmy.config.WxConfig;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class PayUtils {
    private static final Logger logger = LoggerFactory.getLogger(PayUtils.class);

    /**
     * 统一下单
     *
     * @return 统一下单 id
     * @throws Exception 异常处理
     */
    public static Result<Map<String, String>> createPrepayId(String appId, String mchId, String mchKey, String openid, Integer total_fee, String out_trade_no, String body, String ip, String notify_url, String trade_type) throws Exception {
        // 统一下单地址
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        Map<String, String> params = new HashMap<>();

        // 组合统一下单各种参数
        params.put("appid", appId); // 公众账号ID
        params.put("body", body); // 商品描述
        params.put("mch_id", mchId); // 商户号
        params.put("nonce_str", RandomStringUtils.randomAlphanumeric(32)); // 随机字符串
        params.put("notify_url", notify_url); // 通知地址

        if (StringUtils.isExist(openid)) {
            params.put("openid", openid); // 用户标识
        }

        params.put("out_trade_no", out_trade_no); // 商户订单号
        params.put("sign_type", "MD5"); // 签名类型
        params.put("spbill_create_ip", ip); // 终端IP
        params.put("total_fee", String.valueOf(total_fee)); // 标价金额
        params.put("trade_type", trade_type); // 交易类型

        // ASCII 排序
        StringBuilder sortedParams = SortUtils.mapByAscii(params);
        sortedParams.append("&key=").append(mchKey);
        String sign = WXPayUtil.MD5(sortedParams.toString());
        params.put("sign", sign);

        System.out.println(JemmyJson.toJSONString(params));

        return post(url, WXPayUtil.mapToXml(params));
    }

    public static Result<Map<String, String>> createCorporatePayment(String appId, String mchId, String partner_trade_no, String openid, Integer amount, String desc, String ip) throws Exception {
        // 统一下单地址
        String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

        Map<String, String> params = new HashMap<>();

        // 组合统一下单各种参数
        params.put("mch_appid", appId); // 商户号 ID
        params.put("mchid", mchId); // 商户号
        params.put("nonce_str", RandomStringUtils.randomAlphanumeric(32)); // 随机字符串
        params.put("check_name", "NO_CHECK"); // 校验用户姓名选项
        params.put("openid", openid); // 用户标识
        params.put("partner_trade_no", partner_trade_no); // 商户订单号
        params.put("sign_type", "MD5"); // 签名类型
        params.put("amount", String.valueOf(amount)); // 金额
        params.put("desc", desc); // 企业付款备注
        params.put("spbill_create_ip", ip);

        // ASCII 排序
        StringBuilder sortedParams = SortUtils.mapByAscii(params);
        sortedParams.append("&key=").append(WxConfig.mchKey);
        String sign = WXPayUtil.MD5(sortedParams.toString());
        params.put("sign", sign);

        Map<String, String> res = WXPayUtil.xmlToMap(WxPayUtil.doPostDataWithCert(url, WXPayUtil.mapToXml(params), mchId, WxConfig.certificate));

        if (res.get("return_code").equals("SUCCESS")) {
            return Result.<Map<String, String>>of(ResultCode.HTTP200).setData(res);
        }

        return Result.<Map<String, String>>of(ResultCode.HTTP400).putMessage(res.get("return_msg"));
    }

    /**
     * post 请求
     *
     * @param httpsUrl url
     * @param xmlStr   xml 参数
     * @return 请求结果
     */
    public static Result<Map<String, String>> post(String httpsUrl, String xmlStr) {
        try {
            HttpsURLConnection urlCon;
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-Length",
                    String.valueOf(xmlStr.getBytes().length));
            urlCon.setUseCaches(false);
            urlCon.getOutputStream().write(xmlStr.getBytes(StandardCharsets.UTF_8));
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }

            Map<String, String> res = WXPayUtil.xmlToMap(stringBuilder.toString());

            if (res.get("return_code").equals("SUCCESS")) {
                return Result.<Map<String, String>>of(ResultCode.HTTP200).setData(res);
            }

            return Result.<Map<String, String>>of(ResultCode.HTTP400).putMessage(res.get("return_msg"));
        } catch (Exception e) {
            return Result.<Map<String, String>>of(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }
}
