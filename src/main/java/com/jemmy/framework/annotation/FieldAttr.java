package com.jemmy.framework.annotation;

import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.controller.JpaController;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldAttr {
    // 控制器
    Class<? extends JpaController> controller() default JpaController.class;

    // 参数名
    String key() default "";

    // 是否为唯一
    boolean unique() default false;

    // 是否为合并唯一
    boolean unite() default false;

    // cookie 名称
    String cookie() default "";

    // 是否需要验证
    boolean verify() default false;

    // 是否可为空
    boolean empty() default true;

    // 是否更新，如存在唯一值时
    boolean update() default false;

    // 字段名称
    String value() default "";

    boolean json() default false;

    FieldType type() default FieldType.Auto;

    // 序列，值越大越靠前
    int sequence() default 0;

    // 过滤筛选
    boolean filter() default false;

    // 开启自动搜索
    boolean search() default false;
}
