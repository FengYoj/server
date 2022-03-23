package com.jemmy.framework.admin.dao;

import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@EntityAttr("程序异常")
public class Exception extends EntityKey {

    @FieldAttr("信息")
    @Column(columnDefinition = "TEXT")
    private String message;

    @FieldAttr("路径")
    private String path;

    @FieldAttr("类名")
    private String className;

    @FieldAttr("文件名")
    private String fileName;

    @FieldAttr("行数")
    private Integer lineNumber;

    @FieldAttr("方法名称")
    private String methodName;

//    @Column(name = "is_read")
//    private Boolean read = false;

    @FieldAttr("类型")
    private String type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

//    public Boolean getRead() {
//        return read;
//    }
//
//    public void setRead(Boolean read) {
//        this.read = read;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
