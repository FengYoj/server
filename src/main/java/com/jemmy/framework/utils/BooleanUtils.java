package com.jemmy.framework.utils;

public class BooleanUtils {

    public static boolean is(Class<?> clazz) {
        return Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz);
    }

}
