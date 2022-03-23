package com.jemmy.framework.aop.token;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheckerToken {
    boolean status() default false;

    String value() default "admin_token";

    String check() default "";

    boolean isAuto() default false;

    // 响应页面
    String page() default "error/400";
}
