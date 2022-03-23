package com.jemmy.framework.auto.processor;

public enum ProcessorType {
    PHONE("phone"),
    PRICE("price");

    ProcessorType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
