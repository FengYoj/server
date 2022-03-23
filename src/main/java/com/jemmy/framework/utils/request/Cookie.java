package com.jemmy.framework.utils.request;

public class Cookie extends javax.servlet.http.Cookie {

    public Cookie(String name, String value) {
        super(name, value);
    }

    public Cookie(String name, String value, String path) {
        super(name, value);
        this.setPath(path);
    }
}
