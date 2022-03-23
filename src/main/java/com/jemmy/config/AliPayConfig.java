package com.jemmy.config;

import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.MenuType;

@AutoConfig(value = "支付宝支付配置", menu = MenuType.SETTING)
public interface AliPayConfig {
    String appId = "";

    String publicKey = "";

    String privateKey = "";

    String url = "https://openapi.alipay.com/gateway.do";

    String charset = "UTF-8";

    String sign_type = "RSA2";
}
