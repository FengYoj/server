package com.jemmy.framework.admin.dao;

import com.jemmy.framework.controller.EntityKey;
import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class SuperAuthority extends EntityKey {

    @NotNull
    private String ip;

    @NotNull
    @Column(name = "server_key")
    private String key;

    // 备注
    private String remark;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
