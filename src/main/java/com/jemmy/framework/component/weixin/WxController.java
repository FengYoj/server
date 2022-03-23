package com.jemmy.framework.component.weixin;

import com.alibaba.fastjson.JSONObject;
import com.jemmy.config.WxConfig;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.Uri;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpRequest;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RestController
@RequestMapping("WebAPI/WeiXin")
public class WxController {
    private final Request request = new Request();

    @GetMapping("GetSessionKey")
    public Result<JemmyJson> getSessionKey(@AutoParam String code, @AutoParam(required = false, defaults = "weApp", verify = {"we", "mp"}) String platform) {

        Uri uri;

        if (platform.equals("we")) {
            uri = Uri.of(WxUrl.code2Session).setParam("appid", WxConfig.weAppId).setParam("secret", WxConfig.weAppSecret).setParam("js_code", code).setParam("grant_type", "authorization_code");
        } else {
            uri = Uri.of(WxUrl.code2Session).setParam("appid", WxConfig.mpAppId).setParam("secret", WxConfig.mpSecret).setParam("js_code", code).setParam("grant_type", "authorization_code");
        }

        var httpRequest = HttpRequest.newBuilder()
                .uri(uri.build())
                .GET()
                .build();

        return request.send(httpRequest).toJsonStatus();
    }

    @PostMapping("PhoneNumberDecrypt")
    public Result<Object> decrypt(@AutoParam String encryptedData, @AutoParam String iv, @AutoParam String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Base64.Decoder decoder = Base64.getDecoder();

        byte[] sessionKeyBytes = decoder.decode(key);
        byte[] ivBytes = decoder.decode(iv);
        byte[] encryptedBytes = decoder.decode(encryptedData);

        // JDK does not support PKCS7Padding, use PKCS5Padding instead
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec skeySpec = new SecretKeySpec(sessionKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        byte[] ret = cipher.doFinal(encryptedBytes);

        return Result.of(ResultCode.HTTP200).setData(JSONObject.parse(ret));
    }
}
