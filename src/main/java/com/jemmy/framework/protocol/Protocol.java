package com.jemmy.framework.protocol;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Protocol extends EntityKey {

    @FieldAttr(value = "标识符", empty = false, unique = true, update = true)
    private String identifier;

    @FieldAttr(value = "名称", empty = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    @FieldAttr(value = "协议内容", type = FieldType.RichText, empty = false)
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
