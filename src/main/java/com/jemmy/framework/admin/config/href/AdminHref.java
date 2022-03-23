package com.jemmy.framework.admin.config.href;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jemmy.framework.admin.config.href.entity.Menu;
import com.jemmy.framework.admin.config.href.entity.Page;
import com.jemmy.framework.admin.config.href.entity.Submenu;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminHref {
    private List<Menu> menus;

    private List<Submenu> submenus;

    private List<Page> pages;

    private Map<String, Error> error;

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public List<Submenu> getSubmenus() {
        return submenus;
    }

    public void setSubmenus(List<Submenu> submenus) {
        this.submenus = submenus;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public Map<String, Error> getError() {
        return error;
    }

    public void setError(Map<String, Error> error) {
        this.error = error;
    }
}
