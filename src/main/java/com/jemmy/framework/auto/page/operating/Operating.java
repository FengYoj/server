package com.jemmy.framework.auto.page.operating;

import org.springframework.web.bind.annotation.RequestMethod;

public class Operating {

    private String title;

    private OperatingType type;

    private Message msg;

    /**
     *  路径修饰符： *{api} = API , *{page} = PAGE
     *  参数修饰符：&{uuid} = entity.uuid
     */
    private String url;

    private String background = "#00b3d9";

    private String color = "#fff";

    private String filter;

    private OperatingConfig config;

    private RequestMethod method = RequestMethod.GET;

    public Operating(OperatingType type) {
        this.type = type;
    }

    public Operating(OperatingType type, String title) {
        this.title = title;
        this.type = type;
    }

    public Operating(OperatingType type, String title, String url) {
        this.title = title;
        this.url = url;
        this.type = type;
    }

    public Operating(OperatingType type, String title, String url, Message msg) {
        this.title = title;
        this.url = url;
        this.type = type;
        this.msg = msg;
    }

    public Operating(OperatingType type, RequestMethod method, String title, String url, Message msg, String filter) {
        this.title = title;
        this.method = method;
        this.url = url;
        this.type = type;
        this.msg = msg;
        this.filter = filter;
    }

    public Operating(OperatingConfig config, String title, String url, String filter) {
        this.type = config.getType();

        this.config = config;
        this.title = title;
        this.url = url;
        this.filter = filter;
    }

    public Operating() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public OperatingType getType() {
        return type;
    }

    public void setType(OperatingType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public OperatingConfig getConfig() {
        return config;
    }

    public void setConfig(OperatingConfig config) {
        this.config = config;
    }
}
