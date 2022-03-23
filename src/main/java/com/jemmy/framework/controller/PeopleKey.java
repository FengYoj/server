package com.jemmy.framework.controller;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Embeddable
public class PeopleKey implements Serializable {
    @Id
    @TableAttr(width = 100, sort = true, sequence = 10)
    @FieldAttr("ID")
    @CreateAttr(disable = true)
    private Long id;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    @TableAttr(disable = true)
    @CreateAttr(disable = true)
    private String uuid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
