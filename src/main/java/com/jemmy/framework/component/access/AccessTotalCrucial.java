package com.jemmy.framework.component.access;

import com.jemmy.framework.component.statistics.StatisticsCrucial;
import com.jemmy.framework.component.statistics.StatisticsType;

import java.lang.reflect.Field;

public class AccessTotalCrucial extends StatisticsCrucial {
    @Override
    public StatisticsType type() {
        return StatisticsType.ALL;
    }
}
