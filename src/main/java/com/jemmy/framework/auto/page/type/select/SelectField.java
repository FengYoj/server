package com.jemmy.framework.auto.page.type.select;

import com.jemmy.framework.utils.value.StringListValue;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelectField {
    String field() default "";

    String value() default "";

    String[] fixed() default {};

    Class<? extends StringListValue> variable() default StringListValue.class;

    String filter() default "";
}
