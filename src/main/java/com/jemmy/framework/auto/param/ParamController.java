package com.jemmy.framework.auto.param;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.component.json.JemmyArray;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.utils.*;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.request.CookieUtils;
import com.jemmy.framework.utils.request.RequestParamUtils;
import com.jemmy.framework.utils.request.RequestUtils;
import com.jemmy.framework.utils.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class ParamController implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AutoParam.class);
    }

    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, @NonNull NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ResultException {
        AutoParam autoParam = methodParameter.getParameterAnnotation(AutoParam.class);

        if (autoParam != null) {

            // 参数类型
            Class<?> type = methodParameter.getParameterType();

            JemmyJson params;

            // 获取所有参数属性
            switch (autoParam.method()) {
                case COOKIE:
                    params = RequestParamUtils.getCookieParams();
                    break;
                case HEADER:
                    params = RequestParamUtils.getHeaderParams();
                    break;
                default:
                    params = RequestParamUtils.getWrapperParams();
            }

            // 参数名称
            String name = methodParameter.getParameterName();

            switch (autoParam.type()) {
                case AUTO:
                    return autoMethod(autoParam, methodParameter, type, params, name);
                case JSON:
                    return params;
                case PARAM:
                    Object param = params.get(StringUtils.defaults(autoParam.value(), methodParameter.getParameterName()));

                    // 最后根据参数名返回参数值
                    if (autoParam.required() && param == null) {
                        throw new ResultException(name + " cannot be empty");
                    }

                    return Converter.cast(param == null ? StringUtils.isBlank(autoParam.defaults()) ? null : autoParam.defaults() : param, type);
                case ID_TO_ENTITY:
                    // 首先判断是否为 ID 转 实体
                    if (params.containsKey(name + "_id")) {
                        return idToEntity(type, params.getString(name + "_id"));
                    }

                    if (params.containsKey(name) && params.get(name) instanceof String) {
                        return idToEntity(type, params.getString(name));
                    }

                    // 是否允许为空，为空返回 null
                    if (autoParam.required()) {
                        // 返回异常信息
                        throw new ResultException(String.format("%s cannot be empty", name));
                    }

                    return null;
                case PARAM_TO_ENTITY:
                    return params.toJavaObject(type);
            }

        }

        throw new ResultException(methodParameter.getParameterName() + " cannot be empty");
    }

    private Object autoMethod(AutoParam autoParam, MethodParameter methodParameter, Class<?> type, JemmyJson params, String name) throws InvocationTargetException, NoSuchMethodException, InstantiationException, ResultException, IllegalAccessException {
        // 是否为实体类
        if (EntityKey.class.isAssignableFrom(type)) {
            // 首先判断是否为 ID 转 实体
            if (params.containsKey(name + "_id")) {
                return idToEntity(type, params.getString(name + "_id"));
            } else if (params.containsKey(name) && params.get(name) instanceof String) {
                return idToEntity(type, params.getString(name));
            } else {
                // 参数 转 实体类
                return paramToEntity(type, params);
            }
        }

        // 类型为枚举类型时
        else if (type.isEnum()) {
            Object param = params.get(name);

            // 当参数为空
            if (param == null) {
                // 是否为必须值
                if (autoParam.required()) {
                    // 返回异常信息
                    throw new ResultException(String.format("%s cannot be empty", name));
                }

                // 返回空值
                return null;
            }

            // 获取所有枚举值
            Object[] objects = type.getEnumConstants();

            // 遍历查找
            for (Object obj : objects) {
                // 判断 name 是否相同
                if (((Enum<?>) obj).name().equals(param)) {
                    return obj;
                }
            }

            // 没有匹配值，判断是否为必须值
            if (autoParam.required()) {
                throw new ResultException(name + " has no matching value");
            }

            // 返回空值
            return null;
        }

        // 是否为 JSON 参数类型
        else if (type.equals(JemmyJson.class)) {
            if (params.containsKey(name)) {
                return params.getJemmyJson(name);
            }

            if (autoParam.required()) {
                throw new ResultException(name + " cannot be empty");
            }

            return null;
        }

        String key = StringUtils.defaults(autoParam.value(), methodParameter.getParameterName());

        if (List.class.isAssignableFrom(type)) {
            ParameterizedType t = (ParameterizedType) methodParameter.getGenericParameterType();

            JemmyArray array = params.getJemmyArray(key);

            if (array == null || array.size() <= 0) {
                if (autoParam.required()) {
                    throw new ResultException(name + " cannot be empty");
                } else {
                    return null;
                }
            }

            // 获取 List 类型
            Class<?> clazz = (Class<?>) t.getActualTypeArguments()[0];

            // 是否为实体类型
            if (EntityKey.class.isAssignableFrom(clazz)) {

                JemmyArray entityArray = new JemmyArray();

                // 获取控制器
                JpaController<EntityKey, ?> controller = ((EntityKey) clazz.getConstructor().newInstance()).getController();

                // 循环遍历参数
                for (Object obj : array) {

                    // 数组值为字符串类型，查找 uuid
                    if (obj instanceof String) {
                        Result<?> result = controller.controller.findByUuid((String) obj);

                        if (!result.isNormal()) {
                            throw new ResultException(name + " does not exist");
                        }

                        entityArray.add(result.getData());
                    } else {
                        entityArray.add(obj);
                    }
                }

                array = entityArray;
            }

            return array.toJavaList(t);
        }

        Object param = params.get(key);

        // 最后根据参数名返回参数值
        if (param == null && autoParam.required()) {
            throw new ResultException(name + " cannot be empty");
        }

        // 赋予默认值
        Object value = param == null ? StringUtils.isBlank(autoParam.defaults()) ? null : autoParam.defaults() : param;

        // 参数类型不为 Object 时，进行类型转换
        if (!type.getSimpleName().equals("Object")) {
            value = Converter.cast(value, type);
        }

        if (autoParam.verify().length > 0) {
            this.verifyValue(name, autoParam.verify(), value);
        }

        return value;
    }

    private Object paramToEntity(Class<?> type, JemmyJson params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, ResultException {
        Object entity = type.getConstructor().newInstance();

        for (Field field : EntityUtils.getFields(entity.getClass())) {
            FieldAttr fieldAttr = field.getAnnotation(FieldAttr.class);

            // 对私有字段的访问取消权限检查
            field.setAccessible(true);

            if (EntityKey.class.isAssignableFrom(field.getType())) {
                // 属性值
                String param = null;
                // 属性名
                String key = null;

                if (fieldAttr != null) {
                    if (!fieldAttr.cookie().isBlank()) {
                        param = CookieUtils.get(RequestUtils.getServlet(), fieldAttr.cookie());
                        key = fieldAttr.cookie() + " cookie";
                    } else if (!StringUtils.isBlank(fieldAttr.key())){
                        param = fieldAttr.key();
                        key = field.getName();
                    }
                }

                if (param == null && params.containsKey(field.getName())) {
                    Object p = params.get(field.getName());

                    if (p instanceof String) {
                        param = params.get(field.getName());
                        key = field.getName();
                    } else {
                        field.set(entity, params.get(field.getName(), field.getType()));
                        continue;
                    }
                }

                if (param == null && params.get(field.getName()) instanceof String) {
                    param = params.get(field.getName());
                    key = field.getName();
                }

                if (param == null) {
                    param = params.get(field.getName() + "_id");
                    key = field.getName() + "_id";
                }

                // 属性值为空时跳过
                if (StringUtils.isBlank(param)) {
                    continue;
                }

                Result<?> result = ((EntityKey) field.getType().getConstructor().newInstance()).getController().controller.findByUuid(param);

                if (!result.isNormal()) {
                    throw new ResultException(field.getName() + " does not exist");
                }

                field.set(entity, result.getData());
            } else {
                if (List.class.isAssignableFrom(field.getType())) {
                    JemmyArray list = params.getJemmyArray(field.getName());

                    if (list == null) {
                        field.set(entity, null);
                    } else {
                        Class<?> clazz = ListUtils.getGenericType(field);

                        if (EntityKey.class.isAssignableFrom(clazz)) {
                            for (int i = 0; i < list.size(); i++) {
                                Object v = list.get(i);

                                if (v instanceof String) {
                                    Result<?> result = ((EntityKey) clazz.getConstructor().newInstance()).getController().controller.findByUuid((String) v);

                                    if (result.isBlank()) {
                                        throw new ResultException(field.getName() + " does not exist");
                                    }

                                    list.set(i, result.getData());
                                }
                            }
                        }

                        field.set(entity, list.toJavaList(ListUtils.getGenericType(field)));
                    }
                } else if (params.containsKey(field.getName())) {
                    field.set(entity, params.get(field.getName(), field.getType()));
                }
            }
        }

        return entity;
    }

    private Object idToEntity(Class<?> type, String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ResultException {
        if (StringUtils.isBlank(id)) {
            throw new ResultException(type.getSimpleName() + "_id cannot be empty");
        }

        JpaController<?, ?> controller = ((EntityKey) type.getConstructor().newInstance()).getController();

        Result<?> result = controller.controller.findByUuid(id);

        if (result.isNormal()) {
            return result.getData();
        }

        throw new ResultException(type.getSimpleName() + "_id corresponding data does not exist");
    }

    /**
     * 校验属性值
     */
    private void verifyValue(String name, String[] verify, Object value) throws ResultException {

        boolean err = false;

        if (value instanceof Integer) {
            boolean exist = false;
            for (String s : verify) {
                // 判断是否包含
                if (Integer.valueOf(s).equals(value)) {
                    exist = true;
                    break;
                }
            }
            err = !exist;
        } else if (value instanceof String) {
            err = !Arrays.asList(verify).contains(value);
        }

        // 存在参数错误时抛出异常，返回前端
        if (err) {
            throw new ResultException(String.format("'%s' parameter is invalid", name));
        }
    }
}
