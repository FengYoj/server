package com.jemmy.framework.component.access;

import com.jemmy.framework.component.statistics.StatisticsCrucial;

import java.lang.reflect.Field;

public class AccessUserCrucial extends StatisticsCrucial {

    @Override
    public Field field() throws NoSuchFieldException {
        return Access.class.getDeclaredField("user");
    }
}
