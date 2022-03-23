package com.jemmy.framework.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationUtils {
    public static Field getField(Class<?> clazz, Class<? extends Annotation> annotation) {
        for (Field field : EntityUtils.getFields(clazz)) {
            var a = field.getAnnotation(annotation);
            if (a != null) {
                return field;
            }
        }
        return null;
    }
}
