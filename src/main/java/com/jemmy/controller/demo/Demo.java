package com.jemmy.controller.demo;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity
public class Demo extends EntityKey {

    @FieldAttr("名称")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
