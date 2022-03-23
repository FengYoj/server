package com.jemmy.framework.utils.config;

import java.util.Arrays;

class ConfigType {
    private String type;

    ConfigType(String type) {
        String[] TYPE = new String[]{"JSON", "LIST"};
        if (!Arrays.asList(TYPE).contains(type)) {
            this.type = "JSON";
        } else {
            this.type = type;
        }
    }

    public String getType() {
        return type;
    }
}
