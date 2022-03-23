package com.jemmy.framework.interceptor;

import com.jemmy.framework.aop.Aop;
import com.jemmy.framework.aop.AopInterface;
import com.jemmy.framework.utils.SpringBeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AopInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();

            Aop aop = AnnotationUtils.findAnnotation(method, Aop.class);

            // 无 AOP 拦截
            if (aop == null) {
                return true;
            }

            AopInterface a = SpringBeanUtils.getBean(aop.pointcut());

            return a.advise(request, response, handler);
        }

        return true;
    }
}
