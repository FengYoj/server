package com.jemmy.framework.auto.page.entity;

import lombok.Data;

@Data
public class CreateStepData<T> {

    private CreateStepType type = CreateStepType.ORDINARY;

    private String name;

    private String prompt;

    private T data;

    private String title;

    private String entity;

    private String where;

    private Integer sequence = 0;

    private Boolean list = false;

    public CreateStepData(String title, T data) {
        this.title = title;
        this.data = data;
    }

    public CreateStepData(String title, T data, String name) {
        this.name = name;
        this.data = data;
        this.title = title;
    }

    public CreateStepData(String title, T data, String name, String entity) {
        this.name = name;
        this.data = data;
        this.title = title;
        this.entity = entity;
    }

    public CreateStepData(String title, T data, String name, String entity, CreateStepType type) {
        this.name = name;
        this.data = data;
        this.title = title;
        this.entity = entity;
        this.type = type;
    }

    public CreateStepData(String title, T data, String name, String entity, Boolean list) {
        this.name = name;
        this.data = data;
        this.title = title;
        this.entity = entity;
        this.list = list;
    }

    public Boolean isExist() {
        return this.data != null;
    }
}
