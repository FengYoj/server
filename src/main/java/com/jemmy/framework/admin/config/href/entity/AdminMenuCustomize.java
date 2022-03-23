package com.jemmy.framework.admin.config.href.entity;

public interface AdminMenuCustomize {
    default String form() {
        return null;
    }

    default String table() {
        return null;
    }

    default String link() {
        return null;
    }
}
