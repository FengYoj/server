package com.jemmy.framework.auto.processor;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldProcessor {

    ProcessorType type();

}
