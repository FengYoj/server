package com.jemmy.framework.auto.page.type.label;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LabelField {
    LabelType type() default LabelType.Input;
}
