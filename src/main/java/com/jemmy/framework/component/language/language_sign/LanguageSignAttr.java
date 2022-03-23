package com.jemmy.framework.component.language.language_sign;

import com.jemmy.framework.annotation.FieldAttr;

public class LanguageSignAttr {

    @FieldAttr("名称")
    private String name;

    @FieldAttr("标识符")
    private String sign;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
