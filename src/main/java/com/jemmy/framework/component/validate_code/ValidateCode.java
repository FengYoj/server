package com.jemmy.framework.component.validate_code;

import com.jemmy.framework.auto.api.WebFilterMethod;
import com.jemmy.framework.auto.api.annotation.WebFilter;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ValidateCode {
    private final String id = UUID.randomUUID().toString();

    private final Date createDate = new Date();

    @WebFilter
    private String code;

    // Base64 图像
    private String image;

    // 时效，单位：毫秒
    @WebFilter
    private Long aging;

    @WebFilter
    private String check;

    public ValidateCode() {
    }

    public ValidateCode(String code, Long aging) {
        this.code = code;
        this.aging = aging;
    }

    public ValidateCode(String image, String code, Long aging) {
        this.image = image;
        this.code = code;
        this.aging = aging;
    }
}
