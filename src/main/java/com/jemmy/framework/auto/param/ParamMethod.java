package com.jemmy.framework.auto.param;

public enum ParamMethod {

    BODY("body"),
    COOKIE("cookie"),
    HEADER("header");

    ParamMethod(String method) {
        this.method = method;
    }

    private final String method;

    public String getMethod() {
        return method;
    }
}
