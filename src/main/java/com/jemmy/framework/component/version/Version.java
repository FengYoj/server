package com.jemmy.framework.component.version;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity
public class Version extends EntityKey {

    @FieldAttr("版本号")
    private String version;

    @FieldAttr(value = "标识符", unique = true, empty = false)
    private String name;

    @FieldAttr("名称")
    private String title;

    public Version() {
    }

    public Version(String version, String name, String title) {
        this.version = version;
        this.name = name;
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
