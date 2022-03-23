package com.jemmy.framework.auto.page.annotation.field;

import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.controller.JpaController;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CreateAttr {

    // 初始值
    String value() default "";

    // 是否可编辑
    boolean edit() default true;

    // 字段类型，默认自动
    FieldType type() default FieldType.Auto;

    String field() default "";

    Class<? extends JpaController> controller() default JpaController.class;

    // 长度
    int length() default 255;

    String placeholder() default "";

    // 是否禁用此字段，默认 false
    boolean disable() default false;

    String where() default "";
}
