package com.jemmy.framework.auto.config;

import java.util.Map;

public class ConfigUtils {

    public static <T> T getConfig(Class<T> clazz) {
        return ConfigProcessor.getEntity(clazz);
    }

    public static Map<String, Object> getMap(Class<?> clazz) {
        return ConfigProcessor.getMap(clazz);
    }
}
