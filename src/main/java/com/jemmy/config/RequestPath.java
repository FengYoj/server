package com.jemmy.config;

public enum RequestPath {

    WEB("WebAPI"),
    ADMIN("AdminAPI");

    private final String path;

    RequestPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
