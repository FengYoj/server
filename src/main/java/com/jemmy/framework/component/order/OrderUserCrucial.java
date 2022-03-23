package com.jemmy.framework.component.order;

import com.jemmy.framework.component.statistics.StatisticsCrucial;

import java.lang.reflect.Field;

public class OrderUserCrucial extends StatisticsCrucial {

    @Override
    public Field field() throws NoSuchFieldException {
        return Order.class.getDeclaredField("user");
    }
}
