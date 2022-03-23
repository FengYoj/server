package com.jemmy.framework.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryUtils {

    public static List<Map<String, String>> toList(List<String[]> strings, String[] names) {
        List<Map<String, String>> list = new ArrayList<>();

        for (String[] str : strings) {
            Map<String, String> map = new HashMap<>();

            for (int i = 0; i< str.length; i++) {
                map.put(names[i], str[i]);
            }

            list.add(map);
        }

        return list;
    }
}
