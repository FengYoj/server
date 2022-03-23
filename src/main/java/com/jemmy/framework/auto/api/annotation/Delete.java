package com.jemmy.framework.auto.api.annotation;

import com.jemmy.config.RequestPath;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Delete {
    String value() default "";

    RequestPath[] path() default RequestPath.WEB;
}
