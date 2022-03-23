package com.jemmy.framework.component.access;

import com.jemmy.framework.component.statistics.StatisticsCrucial;
import com.jemmy.framework.component.statistics.StatisticsType;

import java.lang.reflect.Field;

public class AccessTypeCrucial extends StatisticsCrucial {

    @Override
    public StatisticsType type() {
        return StatisticsType.DETAILED;
    }

    @Override
    public Field field() throws NoSuchFieldException {
        return Access.class.getDeclaredField("type");
    }
}
