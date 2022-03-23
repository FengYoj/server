package com.jemmy.framework.component.resources;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceAttr {
    String type() default "file";

    String accept() default "";
}
