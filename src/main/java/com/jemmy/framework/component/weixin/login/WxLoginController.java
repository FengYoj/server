package com.jemmy.framework.component.weixin.login;

import com.jemmy.config.WxConfig;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.weixin.WxUrl;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.Uri;
import com.jemmy.framework.utils.result.Result;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpRequest;

@RestController
@AutoAPI("Login/WX")
public class WxLoginController {

    private final Request request = new Request("errcode", "0", "errmsg");

    @Get(value = "GetOpenidByCode")
    public Result<JemmyJson> wxLogin(@RequestParam String code) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(Uri.of(WxUrl.code2Session).setParam("appid", WxConfig.weAppId).setParam("secret", WxConfig.weAppSecret).setParam("js_code", code).setParam("grant_type", "authorization_code").build())
                .GET()
                .build();

        return request.send(httpRequest).toJsonStatus();
    }

    public Result<JemmyJson> mpLogin(String code) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(Uri.of(WxUrl.mpAccessToken).setParam("appid", WxConfig.mpAppId).setParam("secret", WxConfig.mpSecret).setParam("code", code).setParam("grant_type", "authorization_code").build())
                .GET()
                .build();

        return request.send(httpRequest).toJsonStatus();
    }

    public Result<JemmyJson> getUserInfo(String access_token, String openid) {
        System.out.println(Uri.of(WxUrl.mpUserInfo).setParam("access_token", access_token).setParam("openid", openid).setParam("lang", "zh_CN").toString());

        var httpRequest = HttpRequest.newBuilder()
                .uri(Uri.of(WxUrl.mpUserInfo).setParam("access_token", access_token).setParam("openid", openid).setParam("lang", "zh_CN").build())
                .GET()
                .build();

        return request.send(httpRequest).toJsonStatus();
    }
}
