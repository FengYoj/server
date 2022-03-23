package com.jemmy.framework.utils;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MethodUtils {

    public static List<String> getParamterName(Method method){
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] params = u.getParameterNames(method);

        if (params != null) {
            return Arrays.asList(params);
        }

        return null;
    }
}
