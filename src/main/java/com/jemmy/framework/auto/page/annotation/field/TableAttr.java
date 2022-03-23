package com.jemmy.framework.auto.page.annotation.field;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableAttr {
    // 是否禁用此字段，默认 false
    boolean disable() default false;

    // 宽度
    int width() default 200;

    // 是否开启排序
    boolean sort() default false;

    // 实体指定标题字段
    String title_field() default "";

    // 列表取整
    String list() default "";

    // 序列，值越大越靠前
    int sequence() default 0;

    // 是否返回数据
    boolean get() default true;

    // 复写
    boolean override() default false;
}
