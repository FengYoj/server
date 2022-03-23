package com.jemmy.framework.component.upload;

public class UploadConfig {

    private String name;

    private String folder;

    public UploadConfig() {
    }

    public UploadConfig(String name, String folder) {
        this.name = name;
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
