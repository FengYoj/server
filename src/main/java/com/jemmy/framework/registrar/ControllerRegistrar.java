package com.jemmy.framework.registrar;

import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.controller.JpaController;

import java.util.HashMap;
import java.util.Map;

public class ControllerRegistrar {

    private static final Map<Class<? extends EntityKey>, JpaController<?, ?>> map = new HashMap<>();

    public static void add(Class<? extends EntityKey> entity, JpaController<?, ?> controller) {
        map.put(entity, controller);
    }

    public static <C extends JpaController<?, ?>> C get(Class<?> entity) {
        return (C) map.get(entity);
    }
}
