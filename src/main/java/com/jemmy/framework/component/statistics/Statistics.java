package com.jemmy.framework.component.statistics;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity
public class Statistics extends EntityKey {

    @FieldAttr(unite = true)
    private String name;

    @FieldAttr(unite = true)
    private String crucial;

    @FieldAttr(unite = true)
    private String field;

    @FieldAttr(unite = true)
    private StatisticsType type;

    private Long value = 0L;

    public Statistics() {
    }

    public static Statistics ofField(String name, String field) {
        Statistics s = new Statistics();
        s.setName(name);
        s.setField(field);
        return s;
    }

    public static Statistics ofField(String name, String field, String crucial) {
        Statistics s = new Statistics();
        s.setName(name);
        s.setField(field);
        s.setCrucial(crucial);
        return s;
    }

    public static Statistics of(String name, StatisticsType type) {
        Statistics s = new Statistics();
        s.setName(name);
        s.setType(type);
        return s;
    }

    public static Statistics of(String name, String crucial, StatisticsType type) {
        Statistics s = new Statistics();
        s.setName(name);
        s.setCrucial(crucial);
        s.setType(type);
        return s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCrucial() {
        return crucial;
    }

    public void setCrucial(String crucial) {
        this.crucial = crucial;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public StatisticsType getType() {
        return type;
    }

    public void setType(StatisticsType type) {
        this.type = type;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
