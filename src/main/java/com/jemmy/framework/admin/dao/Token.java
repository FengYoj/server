package com.jemmy.framework.admin.dao;

import com.jemmy.framework.utils.MD5Utils;

import java.util.Date;

public class Token {

    private String token;

    private String nature;

    // 时效，默认30分钟
    private Long period = 1000 * 60 * 30L;

    private Long timestamp;

    private String check;

    private Date updateDate = new Date();

    public void setNature(String nature) {
        this.nature = nature;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public void setToken() {
        this.token = MD5Utils.getMD5UUID();
    }

    public String getNature() {
        return nature;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getToken() {
        return token;
    }

    public Long getPeriod() {
        return period;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
}
