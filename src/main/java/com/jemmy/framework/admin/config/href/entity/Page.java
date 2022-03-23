package com.jemmy.framework.admin.config.href.entity;

import lombok.Data;

@Data
public class Page {
    private String name;

    private String title;

    private String tableDataUrl;

    private String tableUrl;

    private String createDataUrl;

    private String formUrl;

    private String editDataUrl;

    private String type;

    private String[] css;

    private String[] js;
}
