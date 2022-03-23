package com.jemmy.framework.auto.page.entity;

import com.jemmy.framework.auto.page.type.FieldType;

public class TableData {

    private String field;

    private String title;

    private FieldType type;

    private Integer width = 200;

    private Boolean sort = false;

    private Integer sequence = 0;

    private String typeName;

    private PageDataConfig config;

    public TableData(FieldType type, String field, String title) {
        this.type = type;
        this.field = field;
        this.title = title;
    }

    public TableData() {

    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Boolean getSort() {
        return sort;
    }

    public void setSort(Boolean sort) {
        this.sort = sort;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public PageDataConfig getConfig() {
        return config;
    }

    public void setConfig(PageDataConfig config) {
        this.config = config;
    }
}
