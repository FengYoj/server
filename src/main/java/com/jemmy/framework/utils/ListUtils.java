package com.jemmy.framework.utils;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtils {

    public static <T> List<T> concat(List<T> a, List<T> b) {
        Stream<T> res = Stream.concat(a.stream(), b.stream());

        return res.collect(Collectors.toList());
    }

    public static <T> List<T> concat(List<T> a, List<T> b, Boolean deduplication) {
        List<T> res = concat(a, b);

        if (deduplication) {
            return deduplication(res);
        }

        return res;
    }

    public static <T> List<T> deduplication(List<T> target) {
        return target.stream().distinct().collect(Collectors.toList());
    }

    public static <T> List<T> toEntity(List<?> target, Class<T> type) {
        return JSON.parseArray(JSON.toJSONString(target), type);
    }

    public static Class<?> getGenericType(Field field) {
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }

    public static <T> List<T> of(T ...courses) {
        return new ArrayList<>(Arrays.asList(courses));
    }
}
