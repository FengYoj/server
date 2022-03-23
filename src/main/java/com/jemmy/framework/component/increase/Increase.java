package com.jemmy.framework.component.increase;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.Title;
import com.jemmy.framework.controller.SilentEntityKey;

import javax.persistence.Entity;

@Entity
public class Increase extends SilentEntityKey {

    @Title
    @FieldAttr(unique = true, search = true)
    private String id;

    private String prefix;

    private Long number;

    private String name;

    public Increase() {
    }

    public Increase(String name, String prefix, Long number) {
        this.prefix = prefix;
        this.name = name;
        this.number = number;
        this.id = prefix + number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
