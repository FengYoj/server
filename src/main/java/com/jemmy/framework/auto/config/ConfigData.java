package com.jemmy.framework.auto.config;

import com.jemmy.framework.auto.page.entity.CreateData;
import com.jemmy.framework.auto.page.entity.CreateStepData;
import com.jemmy.framework.utils.config.Config;

import java.util.List;

public class ConfigData {
    private Config<?> config;

    private List<CreateStepData<List<CreateData>>> page;

    private Boolean openWeb;

    public ConfigData(Config<?> config, List<CreateStepData<List<CreateData>>> page, Boolean openWeb) {
        this.config = config;
        this.page = page;
        this.openWeb = openWeb;
    }

    public ConfigData() {
    }

    public Config<?> getConfig() {
        return config;
    }

    public void setConfig(Config<?> config) {
        this.config = config;
    }

    public List<CreateStepData<List<CreateData>>> getPage() {
        return page;
    }

    public void setPage(List<CreateStepData<List<CreateData>>> page) {
        this.page = page;
    }

    public Boolean getOpenWeb() {
        return openWeb;
    }

    public void setOpenWeb(Boolean openWeb) {
        this.openWeb = openWeb;
    }
}
