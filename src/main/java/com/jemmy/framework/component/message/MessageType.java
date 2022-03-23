package com.jemmy.framework.component.message;

import com.jemmy.framework.auto.page.annotation.field.Title;

public enum MessageType {

    ERROR("error", "错误"),
    INFO("info", "提示"),
    SUCCESS("success", "成功");

    private final String name;

    @Title
    private final String title;

    MessageType(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}
