package com.jemmy.framework.component.order;

import com.jemmy.framework.component.statistics.StatisticsCondition;

public class OrderStatisticsCondition implements StatisticsCondition {
    @Override
    public String name() {
        return "paymentStatus";
    }

    @Override
    public Object value() {
        return 0;
    }
}
