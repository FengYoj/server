package com.jemmy.framework.utils.file;

import com.jemmy.framework.utils.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

public class FileInfo {

    private final static MimetypesFileTypeMap typeUtils = new MimetypesFileTypeMap();

    private File file;

    private String contentType;

    public FileInfo() {
    }

    public FileInfo(File file) {
        this.file = file;
    }

    public FileInfo(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getContentType() {

        if (StringUtils.isBlank(contentType)) {
            // 获取内容类型
            contentType = typeUtils.getContentType(file);
        }

        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
