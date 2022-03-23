package com.jemmy.framework.auto.page.enums;

public enum MenuIcon {

    DATA("data.svg"),
    USER("user.svg"),
    ORDER("order.svg"),
    MENU("menu.svg"),
    RICH_TEXT("rich_text.svg");

    private final String icon;

    MenuIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
