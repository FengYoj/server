package com.jemmy.framework.auto.admin;

import com.jemmy.framework.admin.config.href.entity.AdminMenuCustomize;
import com.jemmy.framework.admin.config.href.entity.Page;
import com.jemmy.framework.auto.page.entity.AdminMenu;
import com.jemmy.framework.auto.page.enums.MenuIcon;

import java.lang.annotation.*;

/**
 * 管理端菜单
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoMenu {
    /** 标题 */
    String title() default "";

    /** 根目录 */
    String root() default AdminMenu.ROOT;

    /** 目录 */
    String[] directory() default AdminMenu.ROOT;

    /** 图标 */
    MenuIcon icon() default MenuIcon.DATA;

    Class<? extends AdminMenuCustomize> customize() default AdminMenuCustomize.class;
}
