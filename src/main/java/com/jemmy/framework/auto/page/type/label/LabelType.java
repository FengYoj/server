package com.jemmy.framework.auto.page.type.label;

public enum LabelType {
    Input("input"),
    Textarea("textarea");

    LabelType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
