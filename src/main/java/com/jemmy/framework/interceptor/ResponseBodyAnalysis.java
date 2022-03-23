package com.jemmy.framework.interceptor;

import com.jemmy.framework.auto.api.WebFilterMethod;
import com.jemmy.framework.auto.api.annotation.ApiAnnotation;
import com.jemmy.framework.auto.api.annotation.WebFilter;
import com.jemmy.framework.auto.processor.FieldProcessor;
import com.jemmy.framework.auto.processor.ProcessorType;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.ListUtils;
import com.jemmy.framework.utils.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ResponseBodyAnalysis implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, @NonNull Class aClass) {
        return methodParameter.hasMethodAnnotation(ApiAnnotation.class);
    }

    @Override
    public Object beforeBodyWrite(Object o, @NonNull MethodParameter methodParameter, @NonNull MediaType mediaType, @NonNull Class aClass, @NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse) {
        if (o instanceof Result) {
            Object data = ((Result<?>) o).getData();

            if (data == null) {
                return o;
            }

            String path = serverHttpRequest.getURI().getPath();

            Boolean isWebAPI = path.contains("WebAPI");
            Boolean isAdminAPI = path.contains("AdminAPI");

            if (data instanceof List && ((List<?>) data).size() > 0) {
                for (Object item : (List<?>) data) {
                    this.replace(path, item, new ArrayList<>(), isWebAPI, isAdminAPI);
                }
            } if (data instanceof Page && ((Page<?>) data).getSize() > 0) {
                for (Object item : ((Page<?>) data).getContent()) {
                    this.replace(path, item, new ArrayList<>(), isWebAPI, isAdminAPI);
                }
            } if (data instanceof Map && ((Map<?,?>) data).size() > 0) {
                ((Map<?,?>) data).forEach((key, value) -> this.replace(path, value, new ArrayList<>(), isWebAPI, isAdminAPI));
            } else {
                this.replace(path, data, new ArrayList<>(), isWebAPI, isAdminAPI);
            }
        }

        return o;
    }

    private void replace(String path, Object entity, List<String> list, Boolean isWebAPI, Boolean isAdminAPI) {
        try {

            if (entity == null || list.contains(entity.getClass().getName())) {
                return;
            }

            list.add(entity.getClass().getName());

            for (Field field : EntityUtils.getFields(entity.getClass())) {

                field.setAccessible(true);

                if (field.isAnnotationPresent(WebFilter.class)) {

                    WebFilter webFilter = field.getAnnotation(WebFilter.class);

                    if (isWebAPI) {

                        if (webFilter.filter().equals(WebFilterMethod.UUID)) {

                            Class<?> type = field.getType();

                            if (List.class.isAssignableFrom(type) && EntityKey.class.isAssignableFrom(ListUtils.getGenericType(field))) {
                                List<EntityKey> l = (List<EntityKey>) field.get(entity);

                                if (l != null) {
                                    List<String> u = new ArrayList<>();

                                    for (EntityKey e : l) {
                                        u.add(e.getUuid());
                                    }

                                    field.set(entity, u);

                                    continue;
                                }
                            } else if (EntityKey.class.isAssignableFrom(field.getType())) {
                                EntityKey e = (EntityKey) field.get(entity);

                                if (e != null) {
                                    // TODO 写入ID错误，更改为Map实体后赋值
                                    field.set(entity, e.getUuid());
                                    continue;
                                }
                            } else {
                                throw new RuntimeException(String.format("The %s field of the %s is not inherited from the EntityKey", field.getName(), entity.getClass().getName()));
                            }
                        } else if (webFilter.filter().equals(WebFilterMethod.ALL) || Arrays.asList(webFilter.block()).contains(path) || (webFilter.allows().length > 0 && !Arrays.asList(webFilter.allows()).contains(path))) {
                            field.set(entity, null);
                            continue;
                        }
                    }
                }

                if (EntityKey.class.isAssignableFrom(field.getType()) && field.get(entity) != null) {
                    this.replace(path, field.get(entity), list, isWebAPI, isAdminAPI);
                }

                // 字段处理注解
                if (isAdminAPI && field.isAnnotationPresent(FieldProcessor.class)) {
                    FieldProcessor processor = field.getAnnotation(FieldProcessor.class);

                    // 价格字段处理
                    if (processor.type().equals(ProcessorType.PRICE)) {
                        Object price = field.get(entity);

                        if (price instanceof Double) {
                            // 分转元
                            field.set(entity, (Double) price / 100);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
