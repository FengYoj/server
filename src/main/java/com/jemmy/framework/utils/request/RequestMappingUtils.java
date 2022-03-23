package com.jemmy.framework.utils.request;

import com.jemmy.framework.utils.SpringBeanUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestMappingUtils {

    private static final List<String> registerPath = new ArrayList<>();

    private static final RequestMappingHandlerMapping registerMapping = SpringBeanUtils.getBean(RequestMappingHandlerMapping.class);

    public static void registerMapping(Object handler, Method method, String path, RequestMethod ...requestMethod) {

        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(path)
                .mappingName(method.getName())
                .methods(requestMethod)
                .params()
                .headers()
                .consumes()
                .produces();

        if (registerPath.contains(path)) {
            registerMapping.unregisterMapping(builder.build());
        } else {
            registerPath.add(path);
        }

        registerMapping.registerMapping(builder.build(), handler, method);
    }

    public static List<String> getParamterName(Class<?> clazz, String methodName){
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                String[] params = u.getParameterNames(method);

                if (params != null) {
                    return Arrays.asList(params);
                }
            }
        }

        return null;
    }
}
