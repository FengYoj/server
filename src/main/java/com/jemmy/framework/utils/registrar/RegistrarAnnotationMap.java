package com.jemmy.framework.utils.registrar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RegistrarAnnotationMap {
    private Annotation annotation;

    private String path;

    private Method[] method;

    public RegistrarAnnotationMap(Annotation annotation, String path, Method[] method) {
        this.annotation = annotation;
        this.path = path;
        this.method = method;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Method[] getMethod() {
        return method;
    }

    public void setMethod(Method[] method) {
        this.method = method;
    }
}
