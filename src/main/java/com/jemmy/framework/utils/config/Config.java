package com.jemmy.framework.utils.config;

import com.alibaba.fastjson.JSONObject;
import com.jemmy.config.PathConfig;
import com.jemmy.framework.auto.config.PrivateConfig;
import com.jemmy.framework.component.json.JemmyArray;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.ListUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Config<T> {
    private final String path = PathConfig.config;

    private final String name;

    private final Class<T> clazz;

    private String content;

    private JemmyJson json;

    private T entity = null;

    public Config(Class<T> course) {
        name = course.getSimpleName();
        clazz = course;

        // 写入配置文件内容
        setContent();

        // 初始化
        initial();
    }

    /**
     * 初始化数据
     */
    private void initial() {
        // JSON 转 实体类
        setEntity();
    }

    private void setEntity() {
        JemmyJson content = JemmyJson.toJemmyJson(this.content);
        JemmyJson json = new JemmyJson();

        T e;

        try {
            e = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException err) {
            throw new RuntimeException(err);
        }

        for (Field field : EntityUtils.getFields(clazz)) {
            if (content.containsKey(field.getName())) {
                field.setAccessible(true);

                try {
                    Object value;

                    // 是否为数组类型
                    if (field.getType().getName().equals("java.util.List")) {
                        JemmyArray array = content.getJemmyArray(field.getName());
                        // 转换为 List 实体
                        value = array.toJavaList(ListUtils.getGenericType(field));
                    } else {
                        value = content.get(field.getName(), field.getType());
                    }

                    field.set(null, value);

                    // 是否存在私有配置注解
                    if (field.isAnnotationPresent(PrivateConfig.class)) {
                        continue;
                    }

                    json.put(field.getName(), value);
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }

            }
        }

        this.json = json;
        entity = e;
    }

    public T getEntity() {
        return entity;
    }

    public JemmyJson getJson() {
        return json;
    }

    public boolean set(Object json) {
        return setConfigFile(name, JSONObject.toJSONString(json));
    }

    private void setContent() {
        try {
            StringBuilder result = new StringBuilder();

            File file = new File(path + name + ".dat");

            File fileParent = file.getParentFile();

            if (!fileParent.exists() && !fileParent.mkdirs()) {
                throw new RuntimeException("Can't create folder");
            }

            if (!file.exists() && !file.createNewFile()) {
                throw new RuntimeException("Can't create file");
            }

            BufferedReader br = new BufferedReader(new FileReader(file));

            String s;

            while((s = br.readLine()) != null){
                result.append(System.lineSeparator()).append(s);
            }

            br.close();

            content = result.length() > 0 ? AesEncodeUtil.decrypt("ShConfigUtilsKey", BinaryUtils.toString(result.toString())) : this.getDefContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getDefContent() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object o = this.clazz.getConstructor().newInstance();
        JemmyJson json = new JemmyJson();

        for (Field field : EntityUtils.getFields(this.clazz)) {
            json.put(field.getName(), field.get(o));
        }

        return json.toJSONString();
    }

    private boolean setConfigFile(String name, String str) {
        try {
            FileWriter writer;
            writer = new FileWriter(path + name + ".dat");
            writer.write(BinaryUtils.stringToBinary(AesEncodeUtil.encrypt("ShConfigUtilsKey", str)));
            writer.flush();
            writer.close();

            // 写入配置文件内容
            content = str;

            // 写入配置文件后初始化属性
            initial();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}