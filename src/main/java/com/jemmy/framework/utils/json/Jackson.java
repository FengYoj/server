package com.jemmy.framework.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jackson {

    // 定义jackson对象
    private static final ObjectMapper mapper = new ObjectMapper();

    // 将对象转换成json字符串
    public String objectToStr(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将json数据转换成pojo对象
    public <T> T strToObject(String json, Class<T> beanType) {
        try {
            return mapper.readValue(json, beanType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 将json数据转换成pojo对象
    public Map<String, Object> strToMap(String str) {
        try {
            return mapper.readValue(str, mapper.getTypeFactory().constructMapType(HashMap.class,
                    String.class, Object.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 将json数据转换成pojo对象list
    public <T> List<T> jsonToList(String json, Class<T> beanType) {
        JavaType javaType= mapper.getTypeFactory().constructParametricType(List.class, beanType);

        try {
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
