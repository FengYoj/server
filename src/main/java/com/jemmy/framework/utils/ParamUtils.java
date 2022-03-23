package com.jemmy.framework.utils;

import com.jemmy.framework.admin.controller.SuperAuthorityController;
import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.object.Filter;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParamUtils {
    private static final SuperAuthorityController superAuthorityController = SpringBeanUtils.getBean(SuperAuthorityController.class);

    public static <T extends EntityKey> Result<T> callbackSave(T entity) {
        JpaController<T, ?> jpaController = entity.getController();

        // 判断唯一性的条件列表
        List<Filter> wheres = null;
        // 存在唯一值时是否可更新，默认否
        boolean update = false;

        Class<?> clazz = entity.getClass();

        EntityAttr entityMapping = clazz.getAnnotation(EntityAttr.class);

        try {
            // 获取所有字段，包括父类
            for (Field field : EntityUtils.getFields(clazz)) {
                FieldAttr fieldMapping = field.getAnnotation(FieldAttr.class);

                // 对私有字段的访问取消权限检查
                field.setAccessible(true);

                String name = field.getName();
                Object val = field.get(entity);

                // 属性值为空且可以为空时跳过
                if (val == null && (fieldMapping == null || fieldMapping.empty())) {
                    continue;
                }

                if (fieldMapping != null && val != null) {

                    // 合并校验
                    if (fieldMapping.unite()) {
                        if (wheres == null) {
                            wheres = new ArrayList<>();
                        }

                        Filter where = new Filter();

                        if (EntityKey.class.isAssignableFrom(val.getClass())) {
                            where.setKey(name + "_uuid");
                            where.setValue(((EntityKey) val).getUuid());
                        } else {
                            where.setKey(name);
                            where.setValue(val);
                        }

                        wheres.add(where);

                        // 其中一个字段存在更新指令既更新
                        if (!update) {
                            update = fieldMapping.update();
                        }
                    }

                    // 独特校验
                    if (fieldMapping.unique()) {
                        Result<T> unique_result;

                        if (EntityKey.class.isAssignableFrom(val.getClass())) {
                            unique_result = jpaController.controller.findCustomize(name + "_uuid", ((EntityKey) val).getUuid());
                        } else {
                            unique_result = jpaController.controller.findCustomize(name, val);
                        }

                        if (unique_result.isNormal() && (StringUtils.isBlank(entity.getUuid()) || !entity.getUuid().equals(unique_result.getData().getUuid()))) {
                            // 存在相同唯一值时是否更新
                            if (fieldMapping.update()) {
                                return jpaController.controller.callbackUpdate(unique_result.getData().getUuid(), entity);
                            }

                            String msg = name + " field value duplicate";

                            // 拥有超级权限的将返回实体 uuid
                            if (superAuthorityController.allow()) {
                                return Result.<T>of(ResultCode.HTTP201).setData(unique_result.getData()).putMessage(msg);
                            }

                            return Result.<T>of(ResultCode.HTTP400).putMessage(msg);
                        }
                    }
                }

                if (val != null && EntityKey.class.isAssignableFrom(val.getClass())) {
                    JpaController<EntityKey, ?> controller = ((EntityKey) val).getController();
                    // Save field entity
                    val = controller.controller.onlySave((EntityKey) val);
                } else if (List.class.isAssignableFrom(field.getType()) && val != null && ((List<?>) val).size() > 0) {
                    Class<?> type = ListUtils.getGenericType(field);

                    if (EntityKey.class.isAssignableFrom(type)) {
                        JpaController<? extends EntityKey, ?> controller = ((EntityKey) type.getConstructor().newInstance()).getController();

                        List<EntityKey> list = (List<EntityKey>) val;

                        for (int i = 0; i < list.size(); i++) {

                            EntityKey e = list.get(i);

                            if (e.getUuid() == null) {
                                list.set(i, controller.controller.onlySave(e));
                            }
                        }
                    }
                }

                field.set(entity, val);
            }

            // 合并校验，不允许多值相同的情况
            if (wheres != null && !wheres.isEmpty()) {
                Result<T> wheres_result = jpaController.controller.findCustomize(wheres);

                if (wheres_result.isNormal() && (StringUtils.isBlank(entity.getUuid()) || !entity.getUuid().equals(wheres_result.getData().getUuid()))) {

                    // 存在相同唯一值时是否更新
                    if (update) {
                        return jpaController.controller.callbackUpdate(wheres_result.getData().getUuid(), entity);
                    }

                    String msg = wheres.stream().map(Filter::getKey).collect(Collectors.joining(", ")) + " field value duplicate";

                    // 拥有超级权限的将返回实体 uuid
                    if (superAuthorityController.allow()) {
                        return Result.<T>of(ResultCode.HTTP201).setData(wheres_result.getData()).putMessage(msg);
                    }

                    return Result.<T>of(ResultCode.HTTP400).putMessage(msg);
                }
            }

            // 判断当前字段是否为独特字段
            if (entityMapping != null && entityMapping.unique().length > 0) {

                for (String unique : entityMapping.unique()) {

                    Field field = clazz.getDeclaredField(unique);

                    field.setAccessible(true);

                    Result<T> result = jpaController.controller.findCustomize(field.getName(), field.get(entity));

                    // 存在唯一值参数且无合并参数，返回实体 ID
                    if (result.isNormal() && entityMapping.merge().length <= 0) {
                        String msg = field.getName() + " field value duplicate";

                        if (superAuthorityController.allow()) {
                            return Result.<T>of(ResultCode.HTTP201).setData(result.getData()).putMessage(msg);
                        }

                        return Result.<T>of(ResultCode.HTTP400).putMessage(msg);
                    }

                    T data = result.getData();

                    for (String name : entityMapping.merge()) {
                        Field merge_field = clazz.getDeclaredField(name);

                        merge_field.setAccessible(true);

                        if (List.class.isAssignableFrom(merge_field.getType())) {
                            List<Object> list = (List<Object>) EntityUtils.getVal(data.getClass().getDeclaredField(name), data);

                            if (list == null) {
                                list = new ArrayList<>();
                            }

                            EntityUtils.setAttribute(data, name, ListUtils.concat(list, (List<Object>) merge_field.get(entity)));

                            entity = data;
                        }
                    }
                }
            }

            // 保存实体
            return Result.<T>of(ResultCode.HTTP200).setData(jpaController.controller.onlySave(entity));
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends EntityKey> Result<String> save(T entity) {
        Result<T> result = callbackSave(entity);

        if (result.getData() != null) {
            return Result.<String>of(ResultCode.HTTP200).setData(result.getData().getUuid());
        }

        return result.toObject();
    }

    public static void check(String name, Object o) {
        if (o instanceof String && StringUtils.isBlank((String) o)) {
            return;
        } else if (o != null) {
            return;
        }

        throw new ResultException(String.format("Parameter %s cannot be empty", name));
    }
}
