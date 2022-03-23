package com.jemmy.framework.component.increase.record;

import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity
public class IncreaseRecord extends EntityKey {
    private String name;

    private Long number;

    public IncreaseRecord() {
    }

    public IncreaseRecord(String name, Long number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public void increase() {
        this.number++;
    }
}
