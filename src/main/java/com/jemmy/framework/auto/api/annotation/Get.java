package com.jemmy.framework.auto.api.annotation;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.api.Authority;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiAnnotation
public @interface Get {
    String value() default "";

    RequestPath[] path() default RequestPath.WEB;

    Authority authority() default Authority.ALL;
}
