package com.jemmy.framework.auto.api;

public enum WebFilterMethod {
    ALL("ALL"),
    UUID("UUID");

    private final String method;

    WebFilterMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
