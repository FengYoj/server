package com.jemmy.framework.auto.api;

public enum Authority {

    ALL("all"),
    ADMIN("admin"),
    ROOT("root");

    private final String name;

    Authority(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
