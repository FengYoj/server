package com.jemmy.framework.registrar;

import com.jemmy.framework.admin.config.href.AdminHrefController;
import com.jemmy.framework.admin.config.href.entity.AdminMenuCustomize;
import com.jemmy.framework.admin.config.href.entity.Menu;
import com.jemmy.framework.admin.config.href.entity.Page;
import com.jemmy.framework.annotation.ScanBean;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoMenu;
import com.jemmy.framework.auto.page.entity.AdminMenu;
import com.jemmy.framework.interfaces.ScanBeanMethod;
import com.jemmy.framework.interfaces.ScanBeanType;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Component
@ScanBean(value = AutoMenu.class, type = ScanBeanType.ANNOTATION)
public class AutoMenuRegistrar implements ScanBeanMethod {

    @Override
    public boolean check(Object bean) {
        return true;
    }

    @Override
    public void registrar(Object bean) {
        AutoMenu autoMenu = AnnotationUtils.findAnnotation(bean.getClass(), AutoMenu.class);

        if (autoMenu == null) {
            return;
        }

        String name;

        if (Admin.class.isAssignableFrom(bean.getClass())) {
            name = ((Admin<?, ?>) bean).getEntity().getSimpleName();
        } else if (JpaController.class.isAssignableFrom(bean.getClass())) {
            name = ((JpaController<?, ?>) bean).getEntity().getSimpleName();
        } else {
            name = bean.getClass().getSimpleName();
        }

        // 是否存在自定义属性
        if (autoMenu.customize().equals(AdminMenuCustomize.class)) {
            this.setAdminPath(autoMenu, name);
        } else {
            this.setCustomize(autoMenu, name);
        }
    }

    private List<String> getMenus(AutoMenu autoMenu) {
        // 菜单目录
        List<String> directory = List.of(autoMenu.directory());

        List<String> menus;

        String root = autoMenu.root();

        if (!root.equals(AdminMenu.ROOT)) {
            menus = new ArrayList<>();
            menus.add(root);

            if (!directory.contains(AdminMenu.ROOT)) {
                menus.addAll(directory);
            }
        } else if (!directory.contains(AdminMenu.ROOT)) {
            menus = directory;
        } else {
            menus = new ArrayList<>();
        }

        return menus;
    }

    private void setCustomize(AutoMenu autoMenu, String name) {

        AdminMenuCustomize customize;

        try {
            customize = autoMenu.customize().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        Menu menu = new Menu();

        menu.setName(StringUtils.defaults(autoMenu.title(), name));
        menu.setType("data");
        menu.setIcon(autoMenu.icon().getIcon());

        if (StringUtils.isExist(customize.link())) {
            menu.setHref("/admin/#/link?path=" + customize.link());

            // 添加至管理端路径配置中
            AdminHrefController.addData(getMenus(autoMenu), menu);

            return;
        } else {
            menu.setHref("/admin/#/table?name=" + name);
        }

        if (StringUtils.isBlank(customize.form()) && StringUtils.isBlank(customize.table())) {
            setAdminPath(autoMenu, name);

            return;
        }

        Page page = new Page();

        page.setTitle(StringUtils.defaults(autoMenu.title(), name));
        page.setType("data");
        page.setName(name);

        // 是否存在自定义表单页面
        if (StringUtils.isExist(customize.form())) {
            page.setFormUrl(customize.form());
        } else {
            page.setCreateDataUrl(String.format("/%s/GetCreateData", name));
            page.setEditDataUrl(String.format("/%s/FindEntity", name));
        }

        // 是否存在自定义表格页面
        if (StringUtils.isExist(customize.table())) {
            page.setTableUrl(customize.table());
        } else {
            page.setTableDataUrl(String.format("/%s/GetTableData", name));
        }

        // 添加至管理端路径配置中
        AdminHrefController.addData(getMenus(autoMenu), menu, page);
    }

    private void setAdminPath(AutoMenu autoMenu, String name) {
        Menu menu = new Menu();

        menu.setName(StringUtils.defaults(autoMenu.title(), name));
        menu.setType("data");
        menu.setIcon(autoMenu.icon().getIcon());
        menu.setHref("/admin/#/table?name=" + name);

        Page page = new Page();

        page.setTitle(StringUtils.defaults(autoMenu.title(), name));
        page.setType("data");
        page.setCreateDataUrl(String.format("/%s/GetCreateData", name));
        page.setEditDataUrl(String.format("/%s/FindEntity", name));
        page.setName(name);
        page.setTableDataUrl(String.format("/%s/GetTableData", name));

        // 添加至管理端路径配置中
        AdminHrefController.addData(getMenus(autoMenu), menu, page);
    }
}
