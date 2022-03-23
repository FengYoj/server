package com.jemmy.framework.component.weixin.access_token;

import com.alibaba.fastjson.JSONObject;
import com.jemmy.config.WxConfig;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.weixin.WxUrl;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.RequestResult;
import com.jemmy.framework.utils.request.Uri;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class AccessTokenInt {

    public static AccessToken MP = new AccessToken(WxConfig.mpAppId, WxConfig.mpSecret);

    public static AccessToken WE = new AccessToken(WxConfig.weAppId, WxConfig.weAppSecret);

}
