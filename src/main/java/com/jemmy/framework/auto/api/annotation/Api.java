package com.jemmy.framework.auto.api.annotation;

import com.jemmy.config.RequestPath;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiAnnotation
public @interface Api {
    String value() default "";

    RequestMethod[] method() default RequestMethod.GET;

    RequestPath[] path() default RequestPath.WEB;
}
