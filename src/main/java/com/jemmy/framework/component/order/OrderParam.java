package com.jemmy.framework.component.order;

import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.registrar.ControllerRegistrar;
import com.jemmy.framework.utils.ClassUtils;

public class OrderParam {

    private final JemmyJson json;

    public OrderParam(JemmyJson json) {
        this.json = json;
    }

    public Integer getInteger(String key) {
        return getInteger(key, false);
    }

    public Integer getInteger(String key, boolean empty) {
        // 检查
        check(key, empty);

        return json.getInteger(key);
    }

    public String getString(String key) {
        return getString(key, false);
    }

    public String getString(String key, boolean empty) {
        // 检查
        check(key, empty);

        return json.getString(key);
    }

    public <T extends EntityKey> T getEntity(String key, Class<T> entity) {
        return getEntity(key, entity, false);
    }

    public <T extends EntityKey> T getEntity(String key, Class<T> entity, boolean empty) {
        // 检查
        check(key, empty);

        JpaController<T, ?> controller = ControllerRegistrar.get(entity);
        Object value = json.get(key);

        if (value instanceof String) {
            return controller.controller.findByUuid((String) value).get();
        }

        return json.get(key, entity);
    }

    /**
     * 检查
     * @param key Key
     * @param empty 可否为空值
     */
    private void check(String key, boolean empty) {
        // 可以为空时，不检查
        if (empty) {
            return;
        }

        // Key 为空或者 Value 为空时抛出 ResultException
        if (!json.containsKey(key) || ClassUtils.isBlank(json.get(key))) {
            throw new ResultException(String.format("Parameter %s cannot be empty", key));
        }
    }
}
