package com.jemmy.framework.component.weixin.wxpay.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SortUtils {
    public static StringBuilder mapByAscii(Map<String, String> map) {
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        infoIds.sort(Map.Entry.comparingByKey());
        // 构造签名键值对的格式
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> item : infoIds) {
            String key = item.getKey();
            String val = item.getValue();
            if (!val.equals("")) {
                if(i > 0) sb.append("&");
                sb.append(key).append("=").append(val);
                i++;
            }
        }
        return sb;
    }
}
