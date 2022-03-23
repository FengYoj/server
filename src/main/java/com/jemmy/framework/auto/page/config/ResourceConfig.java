package com.jemmy.framework.auto.page.config;

import com.jemmy.framework.auto.page.entity.PageDataConfig;

public class ResourceConfig extends PageDataConfig {

    private String type;

    private Boolean list = false;

    public ResourceConfig() {
    }

    public ResourceConfig(String type) {
        this.type = type;
    }

    public ResourceConfig(String type, Boolean list) {
        this.type = type;
        this.list = list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getList() {
        return list;
    }

    public void setList(Boolean list) {
        this.list = list;
    }
}
