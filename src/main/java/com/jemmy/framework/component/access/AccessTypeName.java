package com.jemmy.framework.component.access;

public enum AccessTypeName {

    API("API", "api");

    private final String name;
    private final String type;

    AccessTypeName(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static String getName(String type) {
        for (AccessTypeName value : values()) {
            if (value.getType().equals(type)) {
                return value.getName();
            }
        }

        return type;
    }
}
