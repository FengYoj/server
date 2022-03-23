package com.jemmy.framework.component.upload;

public enum  UploadType {

    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    FILE("file");

    private final String type;

    private UploadType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
