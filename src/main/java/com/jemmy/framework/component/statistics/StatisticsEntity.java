package com.jemmy.framework.component.statistics;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StatisticsEntity {
    Class<? extends StatisticsCrucial>[] crucial() default StatisticsCrucial.class;
}
