package com.jemmy.framework.utils.json;

import com.jemmy.framework.component.json.JemmyArray;
import com.jemmy.framework.component.json.JemmyJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JsonUtils {

    public static JemmyJson neatJson(JemmyJson target, JemmyJson initiative) {
        for (String key: initiative.keySet()) {
            if (!target.containsKey(key)) {
                target.put(key, initiative.get(key));
            } else {
                target.put(key, neat(target, initiative));
            }
        }

        return target;
    }

    public static JemmyArray neatArray(JemmyArray target, JemmyArray initiative) {
        for (int i = 0, s = initiative.size(); i < s; i++) {

        }

        return target;
    }

    /**
     * 判断对象相等
     * @param initiative 检验对象
     * @param passive 被检验对象
     * @return 检验状态
     */
    public static JsonStatus equalJson(JemmyJson initiative, JemmyJson passive) {
        return equalJson(initiative, passive, null);
    }

    /**
     * 判断数组相等
     * @param initiative 检验数组
     * @param passive 被检验数组
     * @return 检验状态
     */
    public static JsonStatus equalArray(JemmyArray initiative, JemmyArray passive) {
        return equalArray(initiative, passive, null);
    }

    /**
     * 判断对象相等
     * @param initiative 检验对象
     * @param passive 被检验对象
     * @param keys 属性名列表
     * @return 检验状态
     */
    private static JsonStatus equalJson(JemmyJson initiative, JemmyJson passive, List<JsonType> keys) {
        if (initiative.size() != passive.size()) {
            return getSizeError(initiative, passive, keys);
        }

        for (String key: initiative.keySet()) {
            if (!passive.containsKey(key)) {
                return JsonStatus.of(false, "少了" + key + "属性", keys);
            }

            JsonStatus jsonStatus = equal(initiative.get(key), passive.get(key), keys, key, JsonType.JSON);

            if (!jsonStatus.isNormal()) {
                return jsonStatus;
            }
        }

        return JsonStatus.of(true);
    }

    /**
     * 判断数组相等
     * @param initiative 检验数组
     * @param passive 被检验数组
     * @param keys 属性名列表
     * @return 检验状态
     */
    private static JsonStatus equalArray(JemmyArray initiative, JemmyArray passive, List<JsonType> keys) {
        if (initiative.size() != passive.size()) {
            return JsonStatus.of(false, "数量大小不一致", keys, String.valueOf(initiative.size()));
        }

        for (int i = 0, s = initiative.size(); i < s; i++) {
            JsonStatus jsonStatus = equal(initiative.get(i), passive.get(i), keys, String.valueOf(i), JsonType.ARRAY);

            if (!jsonStatus.isNormal()) {
                return jsonStatus;
            }
        }

        return JsonStatus.of(true);
    }

    private static Object neat(Object target_value, Object initiative_value) {
        String target_type = target_value.getClass().getTypeName();
        String initiative_type = initiative_value.getClass().getTypeName();

        if (!target_type.equals(initiative_type)) {
            return initiative_value;
        }

        // 判断数据类型名称
        switch(initiative_type.substring((initiative_type.lastIndexOf(".") + 1))) {
            case "JSONObject":
                return neatJson((JemmyJson) target_value, (JemmyJson) initiative_value);
            case "JSONArray":
                return neatArray((JemmyArray) target_value, (JemmyArray) initiative_value);
            case "String":
                if (((String) initiative_value).isEmpty() && ((String) target_value).isBlank()) {
                    return initiative_value;
                }
            default:
                return null;
        }
    }

    /**
     * 判断相等
     * @param initiative_value 检验值
     * @param passive_value 被检验值
     * @param keys 属性名列表
     * @param key 属性名
     * @param type 属性类型
     * @return 检验状态
     */
    private static JsonStatus equal(Object initiative_value, Object passive_value, List<JsonType> keys, String key, JsonType type) {
        String initiative_type = initiative_value.getClass().getTypeName();
        String passive_type = passive_value.getClass().getTypeName();

        if (!initiative_type.equals(passive_type)) {
            return JsonStatus.of(false, "类型不一致", addKey(keys, key, type), initiative_type);
        }

        // 判断数据类型名称
        switch(initiative_type.substring((initiative_type.lastIndexOf(".") + 1))) {
            case "JSONObject":
                return equalJson((JemmyJson) initiative_value, (JemmyJson) passive_value, addKey(keys, key, type));
            case "JSONArray":
                return equalArray((JemmyArray) initiative_value, (JemmyArray) passive_value, addKey(keys, key, type));
            case "String":
                if (((String) passive_value).isBlank()) {
                    return JsonStatus.of(false, "属性类型不能为空", addKey(keys, key, type));
                }
        }

        return JsonStatus.of(true);
    }

    /**
     * 获取数量大小的错误
     * @param initiative 检验对象
     * @param passive 被检验对象
     * @param keys 父级 key 名称
     * @return 错误状态对象
     */
    private static JsonStatus getSizeError(JemmyJson initiative, JemmyJson passive, List<JsonType> keys) {
        // 属性相对多的
        Set<String> many;
        // 属性相对少的
        JemmyJson less;
        // 比较属性多了还是少了
        boolean isMany = initiative.size() < passive.size();

        if (isMany) {
            many = passive.keySet();
            less = initiative;
        } else {
            many = initiative.keySet();
            less = passive;
        }

        // 循环遍历属性多的对象
        for (String keyName : many) {
            // 判断 key 是否存在
            if (!less.containsKey(keyName)) {
                return JsonStatus.of(false, (isMany ? "多" : "少") + "了 " + keyName + " 属性", keys);
            }
        }

        return JsonStatus.of(false, "属性数量大小不一致", keys);
    }

    /**
     * 添加属性名称
     * @param keys 属性名数组
     * @param key 属性名
     * @param type 属性类型
     * @return 属性名数组
     */
    private static List<JsonType> addKey(List<JsonType> keys, String key, JsonType type) {
        // 数组为空时创建新的数组
        if (keys == null) {
            keys = new ArrayList<>();
        }

        JsonType jsonType = new JsonType().setValue(key).setType(type);

        keys.add(jsonType);

        return keys;
    }
}
