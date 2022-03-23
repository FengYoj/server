package com.jemmy.framework.auto.api;

import com.jemmy.framework.admin.controller.SuperAuthorityController;
import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.object.Filter;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.ListUtils;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.controller.ErrorUtils;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.request.CookieUtils;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import com.jemmy.framework.utils.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WebAPI<T extends EntityKey, C extends JpaController<T, ?>> {

    private final SuperAuthorityController superAuthorityController;

    protected C jpaController;

    public WebAPI() {
        this.superAuthorityController = SpringBeanUtils.getBean(SuperAuthorityController.class);
        this.jpaController = ClassUtils.getBean(this.getClass(), 1);
    }

    public Result<T> findByUuid(String uuid) {
        Result<T> result = jpaController.controller.findByUuid(uuid);

        if (!result.isNormal()) {
            return result;
        }

        T entity = result.getData();

        Class<?> clazz = entity.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            FieldAttr f = field.getAnnotation(FieldAttr.class);

            // 字段值为空，跳过
            if (f == null) {
                continue;
            }

            // 是否需要验证
            if (f.verify()) {
                HttpServletRequest request = getRequest();
                String val;

                // 判断 cookie 值不为空
                if (!StringUtils.isBlank(f.cookie())) {
                    val = CookieUtils.get(request, f.cookie());
                } else {
                    val = request.getParameter(f.key().isBlank() ? field.getName() : f.key());
                }

                try {
                    field.setAccessible(true);

                    if ((EntityKey.class.isAssignableFrom(field.getType()) && !((EntityKey) field.get(entity)).getUuid().equals(val)) ||
                            (field.getType().getName().equals("java.lang.String") && !field.get(entity).equals(val))) {
                        return Result.of(ResultCode.HTTP403);
                    }
                } catch (IllegalAccessException e) {
                    return Result.<T>of(ResultCode.HTTP500).setMessage(e.getMessage());
                }
            }
        }

        return result;
    }

    public Result<String> save(T entity, BindingResult bindingResult) {
        if (ErrorUtils.hasErrors(bindingResult)) {
            return ErrorUtils.getStatus(bindingResult);
        }

        HttpServletRequest request = getRequest();

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

                String name = field.getName();

                if (fieldMapping == null) {
                    continue;
                }

                // 对私有字段的访问取消权限检查
                field.setAccessible(true);

                // 当 cookie 值不为空而且没有控制类时执行 cookie 转属性值
                if (!fieldMapping.cookie().isBlank() && fieldMapping.controller().getName().equals(JpaController.class.getName())) {
                    var cookie = CookieUtils.get(request, fieldMapping.cookie());

                    if (cookie == null || cookie.isBlank()) {
                        return Result.<String>of(ResultCode.HTTP400).putMessage(fieldMapping.cookie() + " cookie is required");
                    }

                    field.set(entity, cookie);
                }

                if (!fieldMapping.controller().getName().equals(JpaController.class.getName())) {
                    // 属性值
                    List<String> params = new ArrayList<>();
                    // 属性名
                    String key;

                    if (!fieldMapping.cookie().isBlank()) {
                        params.add(CookieUtils.get(request, fieldMapping.cookie()));
                        key = fieldMapping.cookie() + " cookie";

                    } else {
                        key = fieldMapping.key();

                        if (key.isEmpty()) {
                            // 先判断是否为数组 id 列表
                            key = name + "_ids";

                            String ids = request.getParameter(key);
                            if (StringUtils.isBlank(ids)) {
                                params.add(request.getParameter(name + "_id"));
                            } else {
                                params.addAll(Arrays.asList(ids.split(",")));
                            }
                        }
                    }

                    // 属性值为空且可以为空时跳过
                    if (params.isEmpty() && fieldMapping.empty()) {
                        continue;
                    }

                    // 属性值为空时返回 400
                    if (params.isEmpty()) {
                        return Result.<String>of(ResultCode.HTTP400).putMessage(key + " can not be empty");
                    }

                    for (String param : params) {
                        Result<?> result = SpringBeanUtils.getBean(fieldMapping.controller()).controller.findByUuid(param);

                        if (!result.isNormal()) {
                            return Result.<String>of(ResultCode.HTTP404).putMessage(name + " does not exist");
                        }

                        if (field.getType().getName().equals("java.util.List")) {
                            List<Object> list = (List<Object>) field.get(entity);

                            if (list == null) {
                                list = new ArrayList<>();
                            }

                            list.add(result.getData());

                            field.set(entity, list);
                        } else {
                            field.set(entity, result.getData());
                        }
                    }
                }

                // 合并校验
                if (fieldMapping.unite()) {
                    Object value = field.get(entity);

                    if (value != null) {
                        if (wheres == null) {
                            wheres = new ArrayList<>();
                        }

                        Filter where = new Filter();
                        where.setKey(name);
                        where.setValue(value);

                        wheres.add(where);
                    }

                    // 其中一个字段存在更新指令既更新
                    if (!update) {
                        update = fieldMapping.update();
                    }
                }

                // 独特校验
                if (fieldMapping.unique()) {
                    Result<T> unique_result = jpaController.controller.findCustomize(name, field.get(entity));

                    if (unique_result.isNormal()) {
                        // 存在相同唯一值时是否更新
                        if (fieldMapping.update()) {
                            return jpaController.controller.update(unique_result.getData().getUuid(), entity);
                        }

                        String msg = name + " field value duplicate";

                        // 拥有超级权限的将返回实体 uuid
                        if (superAuthorityController.allow()) {
                            return Result.<String>of(ResultCode.HTTP201).setData(unique_result.getData().getUuid()).putMessage(msg);
                        }

                        return Result.<String>of(ResultCode.HTTP400).putMessage(msg);
                    }
                }
            }

            // 合并校验，不允许多值相同的情况

            if (wheres != null && wheres.size() > 0) {
                Result<T> wheres_result = jpaController.controller.findCustomize(wheres);

                if (wheres_result.isNormal()) {

                    // 存在相同唯一值时是否更新
                    if (update) {
                        return jpaController.controller.update(wheres_result.getData().getUuid(), entity);
                    }

                    String msg = wheres.stream().map(Filter::getKey).collect(Collectors.joining(", ")) + " field value duplicate";

                    // 拥有超级权限的将返回实体 uuid
                    if (superAuthorityController.allow()) {
                        return Result.<String>of(ResultCode.HTTP201).setData(wheres_result.getData().getUuid()).putMessage(msg);
                    }

                    return Result.<String>of(ResultCode.HTTP400).putMessage(msg);
                }
            }

            // 判断当前字段是否为独特字段
            if (entityMapping != null && entityMapping.unique().length > 0) {

                for (String unique : entityMapping.unique()) {

                    Field field = clazz.getDeclaredField(unique);

                    if (field == null) {
                        continue;
                    }

                    field.setAccessible(true);

                    Result<T> result = jpaController.controller.findCustomize(field.getName(), field.get(entity));

                    // 存在唯一值参数且无合并参数，返回实体 ID
                    if (result.isNormal() && entityMapping.merge().length <= 0) {
                        String msg = field.getName() + " field value duplicate";

                        if (superAuthorityController.allow()) {
                            return Result.<String>of(ResultCode.HTTP201).setData(result.getData().getUuid()).putMessage(msg);
                        }

                        return Result.<String>of(ResultCode.HTTP400).putMessage(msg);
                    }

                    T data = result.getData();

                    for (String name : entityMapping.merge()) {
                        Field merge_field = clazz.getDeclaredField(name);

                        merge_field.setAccessible(true);

                        if (merge_field.getType().getName().equals("java.util.List")) {
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
            jpaController.controller.save(entity);

            return Result.<String>of(ResultCode.HTTP200).setData(entity.getUuid());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return Result.<String>of(ResultCode.HTTP500).setMessage(e.getMessage());
        }
    }

    public Result<?> deleteByUuid(String uuid) {
        return jpaController.controller.deleteByUuid(uuid);
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}
