package com.jemmy.framework.auto.page.operating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jemmy.framework.component.json.JemmyJson;

public class OperatingConfig {

    private OperatingType type;

    @JsonIgnore
    public OperatingType getType() {
        return type;
    }

    public void setType(OperatingType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return JemmyJson.toJSONString(this);
    }
}
