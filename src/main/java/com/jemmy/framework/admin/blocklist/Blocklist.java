package com.jemmy.framework.admin.blocklist;

import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity
@EntityAttr("限制名单")
public class Blocklist extends EntityKey {
    @FieldAttr(value = "IP", empty = false)
    private String ip;

    @FieldAttr("备注")
    private String note;

    @FieldAttr("创建者")
    @CreateAttr(disable = true)
    private String creator;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
