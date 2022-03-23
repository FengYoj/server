package com.jemmy.framework.component.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jemmy.framework.auto.page.annotation.field.Title;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MessageSource {

    SYSTEM("system", "系统通知"),
    REQUEST_INTERCEPTION("request_interception", "请求拦截"),
    OTHER("other", "其他");

    private final String name;

    @Title
    private final String title;

    MessageSource(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public static MessageSource get(String name) {
        for (MessageSource value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }

        return MessageSource.OTHER;
    }
}
