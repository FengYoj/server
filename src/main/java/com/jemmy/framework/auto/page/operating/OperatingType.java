package com.jemmy.framework.auto.page.operating;

public enum OperatingType {

    EDIT("edit"),
    DELETE("delete"),
    JUMP("jump"),
    ENTITY("entity"),
    FILE("file"),
    POPUP("popup"),
    REQUEST("request");

    OperatingType(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
