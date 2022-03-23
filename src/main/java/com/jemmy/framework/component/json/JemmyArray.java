package com.jemmy.framework.component.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class JemmyArray extends ArrayList<Object> {

    // 定义 jackson 对象
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JemmyArray() {
    }

    public JemmyArray(List<?> list) {
        if (list != null && list.size() > 0) {
            super.addAll(list);
        }
    }

    public JemmyArray(String str) {
        try {
            super.addAll(mapper.readValue(str, new TypeReference<>() {}));
        } catch (JsonProcessingException e) {
            throw new JemmyException(e);
        }
    }

    public JemmyJson getJemmyJson(int index) {
        return new JemmyJson(super.get(index));
    }

    public <T> List<T> toJavaList() {
        return mapper.convertValue(this, new TypeReference<>() {});
    }

    public <T> List<T> toJavaList(Class<T> type) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, type);

        return mapper.convertValue(this, javaType);
    }

    public <T> List<T> toJavaList(Type type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);

        return mapper.convertValue(this, javaType);
    }

    public static <T> List<T> toJavaList(String str) {
        try {
            return mapper.readValue(str, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new JemmyException(e);
        }
    }

    public static String toJSONString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
