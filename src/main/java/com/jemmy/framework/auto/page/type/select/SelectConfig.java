package com.jemmy.framework.auto.page.type.select;

import com.jemmy.framework.auto.page.entity.PageDataConfig;

public class SelectConfig extends PageDataConfig {
    private String field;

    private Boolean controller;

    private Object data;

    private SelectField selectField;

    private Boolean multiple = false;

    private Class<?> type;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Boolean getController() {
        return controller;
    }

    public void setController(Boolean controller) {
        this.controller = controller;
    }

    public SelectField getSelectField() {
        return selectField;
    }

    public void setSelectField(SelectField selectField) {
        this.selectField = selectField;
    }

    public Boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
