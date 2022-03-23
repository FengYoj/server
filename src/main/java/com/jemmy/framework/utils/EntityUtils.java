package com.jemmy.framework.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.annotation.field.Title;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.auto.processor.FieldProcessor;
import com.jemmy.framework.auto.processor.ProcessorType;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.resources.Resource;
import com.jemmy.framework.component.resources.ResourceAttr;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.utils.value.StringListValue;
import org.hibernate.collection.internal.PersistentBag;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class EntityUtils {

    public static String getEntityTitle(Class<?> e) {
        EntityAttr entityAttr = e.getDeclaredAnnotation(EntityAttr.class);

        if (entityAttr != null && StringUtils.isExist(entityAttr.value())) {
            return entityAttr.value();
        }

        return e.getSimpleName();
    }

    public static <T extends EntityKey> T initEntity(final T entity) {
        final Date date = new Date();

        entity.setUuid(UUID.randomUUID().toString());
//        entity.setId(date.getTime() + Math.round(Math.random() * 10000));
        entity.setCreatedDate(date);
        entity.setModifiedDate(date);

        return entity;
    }

    public static <T extends EntityKey> T updateEntity(final T entity) {
        entity.setModifiedDate(new Date());

        return entity;
    }

    public static void setAttribute(final Object o, final String field, final Object value) {
        try {
            final Field f = o.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(o, value);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAttribute(final Class<?> o, final String field, final Object value) {
        try {
            final Field f = o.getDeclaredField(field);
            f.setAccessible(true);
            f.set(o, value);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static Field getAttribute(final Object o, final String field) {
        try {
            final Field f = o.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(final Object o, final String name) {
        for (Field field : getFields(o.getClass())) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public static List<Field> getFields(Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();

        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            // 得到父类,然后赋给自己
            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    public static List<Field> getFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        final List<Field> fields = new ArrayList<>();

        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    fields.add(field);
                }
            }
            // 得到父类,然后赋给自己
            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    public static Object getVal(final Field field, final Object t) {
        try {
            final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), t.getClass());
            final Method readMethod = descriptor.getReadMethod();

            return readMethod.invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException ignored) {}

        return null;
    }

    public static Object getVal(final String[] field, final Object t) {
        try {
            Object r = t.getClass().getConstructor().newInstance();

            BeanUtils.copyProperties(t, r);

            for (String f : field) {

                if (r == null) {
                    break;
                }

                r = getVal(f, r);
            }

            return r;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getVal(final String field, final Object t) {
        try {
            final PropertyDescriptor descriptor = new PropertyDescriptor(field, t.getClass());
            final Method readMethod = descriptor.getReadMethod();

            return readMethod.invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(field);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 实体对象 转 Map
     * @param o 实体对象
     * @return Map
     */
    public static Map<String, Object> toMap(final Object o) {

        Map<String, Object> map = new HashMap<>();

        for (Field field : getFields(o.getClass())) {
            field.setAccessible(true);

            try {
                map.put(field.getName(), field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * 获取标题字段值
     * @param entity 实体
     * @return 字符串数值
     */
    public static String getTitleValue(final Object entity) {
        Class<?> clazz = entity.getClass();

        List<String> titles = new ArrayList<>();

        try {
            for (Field f : getFields(clazz)) {
                if (f.isAnnotationPresent(Title.class)) {

                    f.setAccessible(true);

                    if (f.isAnnotationPresent(SelectField.class)) {
                        titles.add(getSelectValue(f, f.get(entity)));
                    } else if (String.class.isAssignableFrom(f.getType())) {
                        titles.add((String) f.get(entity));
                    } else if (!ClassUtils.isBaseType(f.getType()) && !f.getType().equals(entity.getClass())) {
                        titles.add(getTitleValue(f.get(entity)));
                    } else {
                        titles.add(JemmyJson.toJSONString(f.get(entity)));
                    }
                }
            }

            return String.join(" - ", titles);
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException i) {
            throw new RuntimeException(i);
        }
    }

    public static <T> T getSelectValue(Field field, Object value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!field.isAnnotationPresent(SelectField.class)) {
            return null;
        }

        SelectField annotation = field.getAnnotation(SelectField.class);

        if (EntityKey.class.isAssignableFrom(field.getType())) {
            return JemmyJson.toJavaObject(getTitleValue(value));
        } else {
            String[] v;

            if (!annotation.variable().getName().equals(StringListValue.class.getName())) {
                v = annotation.variable().getConstructor().newInstance().getContent();
            } else {
                v = annotation.fixed();
            }

            int i = 0;

            // 是否为 int 类型字段
            boolean isInt = Integer.class.isAssignableFrom(field.getType());

            for (; i < v.length; i++) {
                String[] split = v[i].split(":");

                if (split.length > 1) {
                    if (split[0].equals(value)) {
                        return JemmyJson.toJavaObject(split[1]);
                    }
                } else {
                    if ((isInt ? i : split[0]).equals(value)) {
                        return JemmyJson.toJavaObject(split[0]);
                    }
                }
            }
        }

        return null;
    }

    /**
     * 获取数据表名称
     * @param c 实体类
     * @return 数据表名
     */
    public static String getTableName(final Class<? extends EntityKey> c) {
        if (c.isAnnotationPresent(Entity.class)) {
            Entity e = c.getAnnotation(Entity.class);

            if (StringUtils.isExist(e.name())) {
                return e.name();
            }
        }

        return StringUtils.toUnderline(c.getSimpleName());
    }

    public static Map<String, Object> processField(Object t) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, Object> map = new HashMap<>();

        for (Field field : getFields(t.getClass())) {

            field.setAccessible(true);

            if (field.isAnnotationPresent(ResourceAttr.class)) {
                map.put(field.getName(), field.get(t));

                continue;
            }

            Object value = null;

            Object val = field.get(t);

            if (field.isAnnotationPresent(TableAttr.class)) {
                TableAttr tableField = field.getAnnotation(TableAttr.class);

                if (map.containsKey(field.getName()) && !tableField.override()) {
                    continue;
                }

                if (val != null) {
                    switch (val.getClass().getTypeName()) {
                        case "java.util.ArrayList":

                            if (field.isAnnotationPresent(SelectField.class) || field.isAnnotationPresent(ResourceAttr.class)) {
                                break;
                            }

                            // 判断是否需要进行列表字段连接成字符串
                            if (!StringUtils.isBlank(tableField.list())) {
                                List<String> join = new ArrayList<>();

                                JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(val));

                                for (int i = 0; i < array.size(); i++) {
                                    join.add((String) array.getJSONObject(i).get(tableField.list()));
                                }

                                value = String.join(",", join);
                            } else {
                                value = ((List<?>) val).size();
                            }

                            break;
                        case "org.hibernate.collection.internal.PersistentBag":
                            // 避免连表导致无限引用所使用的类
                            value = ((PersistentBag) val).size();

                            break;
                        default:
                            if (StringUtils.isExist(tableField.title_field())) {

                                String[] strings = tableField.title_field().split("\\.");

                                try {
                                    for (String str : strings) {
                                        if (val == null) {
                                            continue;
                                        }
                                        val = getVal(str, val);
                                    }
                                    value = val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    value = null;
                                }
                            } else {
                                value = val;
                            }
                    }
                }
            }

            if (field.isAnnotationPresent(SelectField.class)) {
                SelectField selectField = field.getAnnotation(SelectField.class);

                // 判断选择类型字段是否为实体类
                if (EntityKey.class.isAssignableFrom(field.getType()) ||
                        (List.class.isAssignableFrom(field.getType()) &&
                                EntityKey.class.isAssignableFrom(ListUtils.getGenericType(field)))
                ) {
                    Object obj = field.get(t);

                    if (obj != null) {
                        if (List.class.isAssignableFrom(obj.getClass())) {
                            List<String> res = new ArrayList<>();

                            for (Object o : (List<?>) obj) {
                                res.add(getTitleValue(o));
                            }

                            // 以逗号合并
                            value = org.apache.commons.lang.StringUtils.join(res, ",");
                        } else {
                            value = getTitleValue(obj);
                        }
                    }
                } else {
                    String[] v;

                    if (selectField.variable() != StringListValue.class) {
                        v = selectField.variable().getConstructor().newInstance().getContent();
                    } else {
                        v = selectField.fixed();
                    }

                    if (val != null) {
                        boolean isKey = false;

                        for (String item : v) {
                            String[] split = item.split(":");

                            if (split.length == 2 && String.valueOf(val).equals(split[0])) {
                                isKey = true;
                                value = split[1];
                                break;
                            }
                        }

                        if (!isKey) {
                            if (val instanceof Integer) {
                                value = v[(Integer) val] ;
                            } else if (List.class.isAssignableFrom(val.getClass())) {
                                List<Object> res = new ArrayList<>();

                                for (Object obj : (List<?>) val) {
                                    for (String str : v) {
                                        // 分隔冒号符
                                        String[] split = str.split(":");

                                        // 长度大于 1 ，即为冒号规则
                                        if (split.length > 1) {
                                            // 下标 0 为属性名
                                            if (split[0].equals(String.valueOf(obj))) {
                                                // 下标 1 为属性值
                                                res.add(split[1]);
                                                break;
                                            }
                                        } else {
                                            if (obj instanceof Integer) {
                                                // 如为整型类型时直接当索引值
                                                res.add(v[(Integer) obj]);
                                            } else if (obj instanceof String) {
                                                // 如为字符串类型时直接当属性值
                                                res.add(obj);
                                            }
                                            break;
                                        }
                                    }
                                }

                                // 以逗号合并
                                value = org.apache.commons.lang.StringUtils.join(res, ",");
                            }
                        }
                    }
                }
            }

            if (val != null && field.isAnnotationPresent(FieldAttr.class)) {
                FieldAttr attr = field.getAnnotation(FieldAttr.class);

                switch (attr.type()) {
                    case Price:
                        BigDecimal bd = null;

                        if (val instanceof Integer) {
                            bd = BigDecimal.valueOf(((Integer) val).doubleValue() / 100);
                        } else if (val instanceof Double) {
                            bd = BigDecimal.valueOf((Double) val / 100);
                        }

                        if (bd == null) {
                            break;
                        }

                        // 保留两位小数
                        value = "￥" + bd.setScale(2, RoundingMode.HALF_UP);

                        break;
                }
            }

            // 原始值不为空，处理值为空，且原始值类型不为基础类型和资源类型，获取实体类 Title 注解的值
            if (val != null && value == null && !ClassUtils.isBaseType(val.getClass()) && !Resource.class.isAssignableFrom(val.getClass())) {
                // 获取 Title 属性
                value = getTitleValue(val);
            }

            if (value == null) {
                value = val;
            }

            // 处理 数值
            value = processValue(field, value);

            map.put(field.getName(), value);
        }

        return map;
    }

    public static List<Map<String, Object>> processField(List<?> content) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object t : content) {
            list.add(processField(t));
        }

        return list;
    }

    /**
     * 处理数值
     * @param field 字段
     * @param value 值
     * @return 处理值
     */
    public static Object processValue(Field field, Object value) {
        if (field.isAnnotationPresent(FieldProcessor.class)) {
            FieldProcessor processor = field.getAnnotation(FieldProcessor.class);

            // 处理机类型
            ProcessorType type = processor.type();

            switch (type.getName()) {
                case "price":
                    BigDecimal bd = null;

                    if (value instanceof Integer) {
                        bd = BigDecimal.valueOf(((Integer) value).doubleValue() / 100);
                    } else if (value instanceof Double) {
                        bd = BigDecimal.valueOf((Double) value / 100);
                    }

                    if (bd == null) {
                        break;
                    }

                    // 保留两位小数
                    value = "￥" + bd.setScale(2, RoundingMode.HALF_UP);

                    break;
                case "phone":
                    if (value instanceof String && StringUtils.isExist((String) value)) {
                        value = ((String) value).replaceAll("^(\\d{3})(\\d{4})(\\d{4})$", "$1-$2-$3");
                    }
                    break;
            }
        }

        return value;
    }

    /**
     * 实体值分配
     * @param target 目标实体
     * @param source 源实体
     */
    public static <T> T assign(T target, T source) {
        JemmyJson json = JemmyJson.toJemmyJson(source);

        for (Field field : getFields(target.getClass())) {
            try {
                Object sv = json.get(field.getName(), field.getType());

                if (field.getName().equals("uuid")) {
                    System.out.println(field.getType());
                    System.out.println(sv.getClass());
                    System.out.println(sv);
                    continue;
                }

                if (sv != null) {

                    field.setAccessible(true);

                    if (!sv.equals(field.get(target))) {
                        field.set(target, sv);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return target;
    }
}
