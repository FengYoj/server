package com.jemmy.framework.component.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jemmy.framework.utils.EntityUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JemmyJson extends HashMap<String, Object> {

    // 定义 jackson 对象
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JemmyJson() {
    }

    public JemmyJson(Map<String, Object> map) {
        super.putAll(map);
    }

    public JemmyJson(Object obj) {
        super.putAll(mapper.convertValue(obj, new TypeReference<>() {}));
    }

    public JemmyJson(String str) {
        try {
            this.setMap(mapper.readValue(str, new TypeReference<>() {}));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(Map<String, Object> map) {
        super.putAll(map);
    }

    public void put(JemmyJson json) {
        super.putAll(json);
    }

    public <T> T get(String key, Class<T> type) {
        return mapper.convertValue(super.get(key), type);
    }

    public <T> T get(String key) {
        return mapper.convertValue(super.get(key), new TypeReference<>() {});
    }

    public JemmyJson getJemmyJson(String key) {
        return new JemmyJson(getMap(key));
    }

    public JemmyArray getJemmyArray(String key) {

        if (!containsKey(key)) {
            return null;
        }

        return new JemmyArray(getList(key));
    }

    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    public Long getLong(String key) {
        return get(key, Long.class);
    }

    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    public String getString(String key) {
        return get(key, String.class);
    }

    public List<Object> getList(String key) {

        Object list = super.get(key);

        if (list == null) {
            return null;
        }

        return mapper.convertValue(list, new TypeReference<>() {});
    }

    public Map<String, Object> getMap(String key) {
        return mapper.convertValue(super.get(key), new TypeReference<>() {});
    }

    public static JemmyJson toJemmyJson(String str) {
        return new JemmyJson(str);
    }

    public static JemmyJson toJemmyJson(Object obj) {
        JemmyJson json = new JemmyJson();

        for (Field field : EntityUtils.getFields(obj.getClass())) {
            try {
                field.setAccessible(true);
                json.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return json;
    }

    public static String toJSONString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T toJavaObject(Class<T> type) {
        return mapper.convertValue(this, type);
    }

    public <T> T toJavaObject() {
        return mapper.convertValue(this, new TypeReference<>() {});
    }

    public static <T> T toJavaObject(Object obj) {
        return mapper.convertValue(obj, new TypeReference<>() {});
    }

    public static <T> T toJavaObject(String obj, Class<T> type) {
        try {
            return mapper.readValue(obj, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSONString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMap(Map<String, Object> map) {
        // 清空 Map 内属性
        super.clear();

        // 赋值
        super.putAll(map);
    }
}
