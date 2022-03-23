package com.jemmy.framework.utils;

import java.lang.reflect.ParameterizedType;

public class ClassUtils {
    public static <T> Class<T> getGeneric(Class<?> clazz, int idx) {
        return (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[idx];
    }

    public static <T> T getBean(Class<?> clazz, int idx) {
        Class<T> c = getGeneric(clazz, idx);
        return SpringBeanUtils.getBean(c);
    }

    private static final Object[] types = new Object[]{String.class, Integer.class, Byte.class, Long.class, Double.class, Float.class, Character.class, Short.class, Boolean.class};

    public static Boolean isBaseType(Class<?> c) {
        for (Object o : types) {
            if (c.equals(o)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isBlank(Object data) {
        if (data == null) {
            return true;
        }

        // 如果为字符串类型，检查是否为空值
        return data instanceof String && ((String) data).isBlank();
    }
}
