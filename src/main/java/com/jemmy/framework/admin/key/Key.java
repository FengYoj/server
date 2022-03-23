package com.jemmy.framework.admin.key;

import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity(name = "authority_keys")
public class Key extends EntityKey {

    @CreateAttr(disable = true)
    private String privateKey;

    private String source;

    private String remark;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
