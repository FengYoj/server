package com.jemmy.framework.component.language;

import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.auto.page.annotation.field.EditorField;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.component.language.language_sign.LanguageSign;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@EntityAttr("语言包")
public class Language extends EntityKey {

    @StepAttr(title = "选择终端标识符")
    @FieldAttr(value = "终端标识符", empty = false, unique = true, filter = true)
    @OneToOne
    @SelectField
    private LanguageSign sign;

    @FieldAttr(value = "语言包", empty = false)
    @EditorField
    @StepAttr(name = "data", title = "语言包数据")
    @Column(columnDefinition = "text")
    @TableAttr(disable = true)
    private String data;

    public Language() {
    }

    public Language(LanguageSign sign, String data) {
        this.sign = sign;
        this.data = data;
    }

    public LanguageSign getSign() {
        return sign;
    }

    public void setSign(LanguageSign sign) {
        this.sign = sign;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
