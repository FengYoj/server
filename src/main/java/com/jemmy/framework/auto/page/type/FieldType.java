package com.jemmy.framework.auto.page.type;

public enum FieldType {
    Phone("phone"),
    Auto("auto"),
    Input("input"),
    Entity("entity"),
    List("list"),
    Select("select"),
    Upload("upload"),
    Number("number"),
    Switch("switch"),
    Editor("editor"),
    Label("label"),
    Password("password"),
    Textarea("textarea"),
    RichText("richtext"),
    Map("map"),
    Subclass("subclass"),
    ENUM("enum"),
    Keyboard("Keyboard"),

    // 资源
    Resource("resource"),

    // 价格
    Price("price");

    FieldType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
