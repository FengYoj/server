package com.jemmy.framework.data.sql.entity.condition;

import lombok.Data;

@Data
public class Condition {

    private String key;

    private Object value;

    private ConditionType type = ConditionType.EQUAL;

    public Condition() {
    }

    public Condition(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Condition(String key, Object value, ConditionType type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s %s", key, getSymbol());
    }

    private String getSymbol() {
        String format = "";

        switch (type) {
            case LIKE:
                return "like '%" + value + "%'";
            case EQUAL:
                format = "= %s";
        }

        return String.format(format, value instanceof String ? ("'" + value + "'") : value);
    }
}
