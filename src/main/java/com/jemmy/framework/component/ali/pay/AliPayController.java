package com.jemmy.framework.component.ali.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.jemmy.config.AliPayConfig;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.stereotype.Component;

@Component
public class AliPayController {

    public Result<String> createAppPay(AlipayTradeAppPayModel model, String notify_url) {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(AliPayConfig.url, AliPayConfig.appId, AliPayConfig.privateKey, "json", AliPayConfig.charset, AliPayConfig.publicKey, AliPayConfig.sign_type);

        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(notify_url);

        try {
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);

            if (response.isSuccess()) {
                System.out.println(response.getBody());
                return Result.<String>of(ResultCode.HTTP200).setData(response.getBody());
            }

            return Result.<String>of(ResultCode.HTTP400).putMessage(response.getMsg());
        } catch (AlipayApiException e) {
            return Result.<String>of(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }
}
