package com.jemmy.framework.auto.page.config;

import com.jemmy.framework.auto.page.entity.PageDataConfig;

public class SubclassConfig extends PageDataConfig {

    private String mappedBy;

    public SubclassConfig(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public SubclassConfig() {
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }
}
