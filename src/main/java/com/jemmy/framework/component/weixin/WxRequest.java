package com.jemmy.framework.component.weixin;

import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.weixin.access_token.AccessToken;
import com.jemmy.framework.component.weixin.access_token.AccessTokenInt;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.RequestResult;
import com.jemmy.framework.utils.request.Uri;

import java.net.http.HttpRequest;

public class WxRequest {

    public static RequestResult get(Uri uri, AccessToken accessToken) {
        uri.setParam("access_token", accessToken.get());

        var request = HttpRequest.newBuilder()
                .uri(uri.build())
                .GET()
                .build();

        RequestResult result = Request.send(request, "errcode", 0, "errmsg", null);

        // 判断 Token 是否过期
        if (result.isBlank() && result.getStatus() == 40001 && result.getMessage().contains("access_token is invalid or not latest")) {
            // 更新 token
            accessToken.update();
            // 重新发起请求
            return get(uri, accessToken);
        }

        return result;
    }

    public static RequestResult post(Uri uri, HttpRequest.BodyPublisher body, AccessToken accessToken) {
        uri.setParam("access_token", accessToken.get());

        var request = HttpRequest.newBuilder()
                .uri(uri.build())
                .POST(body)
                .build();

        RequestResult result = Request.send(request, "errcode", 0, "errmsg", null);

        System.out.println(JemmyJson.toJemmyJson(result.toJsonStatus()));

        if (result.isBlank() && result.getStatus() == 40001 && result.getMessage().contains("access_token is invalid or not latest")) {
            // 更新 token
            accessToken.update();
            // 重新发起请求
            return post(uri, body, accessToken);
        }

        return result;
    }
}
