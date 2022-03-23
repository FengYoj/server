package com.jemmy.framework.auto.config;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface AutoConfig {
    String value() default "";

    boolean web() default false;

    String[] menus() default "";

    // 菜单类型，默认不显示
    MenuType menu() default MenuType.NONE;
}
