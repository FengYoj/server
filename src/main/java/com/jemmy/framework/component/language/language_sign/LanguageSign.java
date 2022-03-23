package com.jemmy.framework.component.language.language_sign;

import com.jemmy.config.LanguageConfig;
import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.Title;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity
@EntityAttr("语言包标识符")
public class LanguageSign extends EntityKey {

    @Title
    @FieldAttr(value = "终端", empty = false, unite = true, filter = true, search = true)
    @SelectField(variable = LanguageConfig.TERMINAL.class)
    private String terminal;

    @FieldAttr(value = "标识符", empty = false, unite = true, filter = true, search = true)
    private String sign;

    @Title
    @FieldAttr(value = "名称", empty = false, search = true)
    private String name;

    public LanguageSign() {
    }

    public LanguageSign(String terminal, String sign, String name) {
        this.terminal = terminal;
        this.sign = sign;
        this.name = name;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
