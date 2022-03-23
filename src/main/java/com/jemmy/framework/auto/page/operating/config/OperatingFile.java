package com.jemmy.framework.auto.page.operating.config;

import com.jemmy.framework.auto.page.operating.OperatingConfig;
import com.jemmy.framework.auto.page.operating.OperatingType;

public class OperatingFile extends OperatingConfig {

    public OperatingFile() {
        this.setType(OperatingType.FILE);
    }

    public OperatingFile(String accept) {
        this.setType(OperatingType.FILE);
        this.accept = accept;
    }

    private String accept;

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
