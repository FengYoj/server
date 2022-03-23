package com.jemmy.framework.component.weixin.access_token;

import com.alibaba.fastjson.JSONObject;
import com.jemmy.config.WxConfig;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.weixin.WxUrl;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.RequestResult;
import com.jemmy.framework.utils.request.Uri;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class AccessToken {

    private String token = null;

    private Integer expires_in = null;

    private Date update;

    private final Request request = new Request("errcode", "0", "errmsg");

    private final String appid;

    private final String secret;

    public AccessToken(String appid, String secret) {
        this.appid = appid;
        this.secret = secret;
    }

    public String update() {
        // 请求获取 token
        getByRequest();
        // 返回 Token
        return token;
    }

    public String get() {
        if (check()) {
            return token;
        }

        // 请求获取 token
        getByRequest();

        return token;
    }

    private boolean check() {
        if (StringUtils.isBlank(token)) {
            return false;
        }

        return update.getTime() > (new Date().getTime() - expires_in * 1000);
    }

    private void getByRequest() {
        var httpRequest = HttpRequest.newBuilder()
                .uri(Uri.of(WxUrl.token).setParam("appid", appid).setParam("secret", secret).setParam("grant_type", "client_credential").build())
                .GET()
                .build();

        RequestResult res = request.send(httpRequest);

        System.out.println(JemmyJson.toJemmyJson(res.toJsonStatus()));

        if (res.isNormal()) {
            JemmyJson json = res.toJson();
            token = json.getString("access_token");
            expires_in = json.getInteger("expires_in");
            update = new Date();
        }
    }

}
