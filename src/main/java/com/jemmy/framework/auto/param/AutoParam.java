package com.jemmy.framework.auto.param;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoParam {
    String value() default "";

    boolean required() default true;

    String defaults() default "";

    ParamMethod method() default ParamMethod.BODY;

    AutoParamType type() default AutoParamType.AUTO;

    // 校验
    String[] verify() default {};
}
