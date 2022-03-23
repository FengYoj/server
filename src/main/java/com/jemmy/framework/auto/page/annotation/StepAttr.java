package com.jemmy.framework.auto.page.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StepAttr {

    String name() default "";

    String title() default "";

    String mappedBy() default "";

    String where() default "";

    int sequence() default 0;

    String prompt() default "";
}
