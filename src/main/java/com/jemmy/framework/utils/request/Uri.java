package com.jemmy.framework.utils.request;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Uri {
    private String url;

    private final List<String> param = new ArrayList<>();

    public Uri() {
    }

    public Uri(String url) {
        this.url = url;
    }

    public Uri(String url, Object... args) {
        this.url = String.format(url, args);
    }

    public Uri setParam(String key, Object param) {
        this.param.add(key + "=" + param);

        return this;
    }

    public URI build() {
        if (param.size() > 0) {
            return URI.create(url + "?" + String.join("&", param));
        }

        return URI.create(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url + "?" +  String.join("&", param);
    }

    public static Uri of(String url) {
        return new Uri(url);
    }

    public static Uri of(String url, Object... args) {
        return new Uri(url, args);
    }
}
