package com.jemmy.framework.component.statistics;

import com.jemmy.framework.controller.EntityKey;
import lombok.Data;

import java.lang.reflect.Field;

@Data
public class StatisticsField {

    private Field field;

    private Boolean entity;

    public StatisticsField() {
    }

    public StatisticsField(Field field) {
        field.setAccessible(true);

        this.field = field;
        this.entity = EntityKey.class.isAssignableFrom(field.getType());
    }
}
