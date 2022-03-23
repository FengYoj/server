package com.jemmy.framework.component.weixin;

public interface WxUrl {
    String code2Session = "https://api.weixin.qq.com/sns/jscode2session";

    String wxacodeunlimit = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";

    String token = "https://api.weixin.qq.com/cgi-bin/token";

    String mpAccessToken = "https://api.weixin.qq.com/sns/oauth2/access_token";

    String mpUserInfo = "https://api.weixin.qq.com/sns/userinfo";

    String createQrcode = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
}
