package com.jemmy.framework.config;

import com.jemmy.framework.admin.controller.TokenController;
import com.jemmy.framework.auto.param.ParamController;
import com.jemmy.framework.interceptor.AdminInterceptor;
import com.jemmy.framework.interceptor.AopInterceptor;
import com.jemmy.framework.interceptor.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    private final TokenController tokenController;

    private final HandlerInterceptor handlerInterceptor;

    public AppConfig(TokenController tokenController, HandlerInterceptor handlerInterceptor) {
        this.tokenController = tokenController;
        this.handlerInterceptor = handlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor).addPathPatterns("/**");

        // Token 拦截器
        registry.addInterceptor(new AdminInterceptor(tokenController))
                // 添加管理端 页面 / API 的拦截
                .addPathPatterns("/admin/**", "/AdminAPI/**")
                // 排除 登录页面 / 登录 API / 静态资源 的拦截
                .excludePathPatterns("/admin/login/**", "/AdminAPI/AdminAccount/Login", "/static/**");

        // AOP 拦截器
        registry.addInterceptor(new AopInterceptor())
                // 添加 所有 拦截
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(paramController());
    }

    @Bean
    public ParamController paramController() {
        return new ParamController();
    }
}
