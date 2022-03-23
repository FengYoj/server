package com.jemmy.framework.component.encryption;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.utils.result.Result;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;

@AutoAPI("Encryption")
public class EncryptionController {

    @Value("${jasypt.encryptor.password}")
    private String salt;

    @Get
    public Result<String> configuration(@AutoParam String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        // 加密所需的 salt (盐)
        textEncryptor.setPassword(salt);
        // 要加密的数据
        return Result.<String>HTTP200().setData(textEncryptor.encrypt(text));
    }

    @Get
    public Result<String> random(@AutoParam(required = false, defaults = "8") Integer size) {
        StringBuilder sb = new StringBuilder();
        String strAll = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < size; i++) {
            int f = (int) (Math.random() * 62);
            sb.append(strAll.charAt(f));
        }
        return Result.<String>HTTP200().setData(sb.toString());
    }
}
