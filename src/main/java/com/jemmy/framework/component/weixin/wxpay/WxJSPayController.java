package com.jemmy.framework.component.weixin.wxpay;

import com.github.wxpay.sdk.WXPayUtil;
import com.jemmy.config.WxConfig;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.order.Order;
import com.jemmy.framework.component.order.OrderRepository;
import com.jemmy.framework.component.weixin.wxpay.utils.PayUtils;
import com.jemmy.framework.component.weixin.wxpay.utils.SortUtils;
import com.jemmy.framework.config.Setting;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.UuidUtils;
import com.jemmy.framework.utils.file.ClassPathResourceReader;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.RequestResult;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

public class WxJSPayController <E extends Order, R extends OrderRepository<E>> {

    private final String NOTIFY_URL;

    private WxPayController<E, R> wxPayController;

    private final JpaController<E, R> controller;

    private final Request request = new Request();

    public WxJSPayController(JpaController<E, R> controller) {
        this.controller = controller;
        this.NOTIFY_URL = String.format("%s/WebAPI/%s/WxPayCallback", Setting.DOMAIN, controller.getEntity().getSimpleName());
        this.wxPayController = new WxPayController<>(controller, this.NOTIFY_URL);
    }

    /**
     * 创建支付订单
     *
     * @param order 订单实体
     * @return 支付对象
     */
    public Result<Map<String, String>> create(E order, HttpServletRequest request) throws NoSuchAlgorithmException, IOException, InvalidKeyException, SignatureException {

//        JemmyJson param = new JemmyJson();
//
//        param.put("appid", WxConfig.mpAppId);
//        param.put("mchid", WxConfig.mchId);
//        param.put("description", order.getName());
//        param.put("out_trade_no", order.getUuid());
//        param.put("notify_url", NOTIFY_URL);
//
//        JemmyJson amount = new JemmyJson();
//        amount.put("total", order.getTotalPrice());
//        param.put("amount", amount);
//
//        JemmyJson payer = new JemmyJson();
//        payer.put("openid", order.getUser().getMpOpenid());
//        param.put("payer", payer);

        return wxPayController.createJSPay(order);
    }

    /**
     * 获取私钥。
     *
     * @return 私钥对象
     */
    public static PrivateKey getPrivateKey() throws IOException {
        String content = Files.readString(ClassPathResourceReader.getFile("apiclient_key.pem").toPath());
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }
}
