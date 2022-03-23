package com.jemmy.framework.component.statistics;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StatisticsAttr {

    Class<? extends StatisticsCondition> condition() default StatisticsCondition.class;

}
