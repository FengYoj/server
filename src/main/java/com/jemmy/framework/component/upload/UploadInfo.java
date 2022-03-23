package com.jemmy.framework.component.upload;

import com.jemmy.framework.component.resources.Resource;

import java.io.File;

public class UploadInfo extends Resource {

    private File file;

    private UploadType uploadType;

    public UploadInfo() {
    }

    public UploadInfo(File file, UploadType uploadType, Long size, String name, String type) {
        this.file = file;
        this.uploadType = uploadType;

        this.setSize(size);
        this.setName(name);
        this.setType(type);
    }

    public UploadType getUploadType() {
        return uploadType;
    }

    public void setUploadType(UploadType uploadType) {
        this.uploadType = uploadType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Boolean delete() {
        return file != null && file.delete();
    }
}
