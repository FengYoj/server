package com.jemmy.framework.component.statistics;

import com.jemmy.framework.controller.EntityKey;

import java.lang.reflect.Field;

public class StatisticsCrucial {

    private Field field = null;

    private Boolean isEntity = null;

    public StatisticsType type() {
        return StatisticsType.TOTAL;
    }

    public Field field() throws NoSuchFieldException {
        return null;
    }

    public Object getValue(Object e) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField();

        if (field == null) {
            return null;
        }

        field.setAccessible(true);

        return field.get(e);
    }

    public Boolean isEntity() {
        if (isEntity == null) {
            Field field = getField();

            if (field == null) {
                return false;
            }

            isEntity = EntityKey.class.isAssignableFrom(field.getType());
        }

        return isEntity;
    }

    public Boolean isInitial() throws NoSuchFieldException {
        return getField() == null;
    }

    public Field getField() {
        if (field == null) {
            try {
                field = field();
            } catch (Exception ignored) {}
        }

        return field;
    }
}
