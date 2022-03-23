package com.jemmy.framework.auto.api.annotation;

import com.jemmy.framework.auto.api.WebFilterMethod;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebFilter {

    String[] allows() default {};

    String[] block() default {};

    // 允许 Web API 访问
    WebFilterMethod filter() default WebFilterMethod.ALL;
}
