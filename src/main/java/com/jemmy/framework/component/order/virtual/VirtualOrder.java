package com.jemmy.framework.component.order.virtual;

import com.jemmy.framework.component.order.Order;
import com.jemmy.framework.component.order.OrderUserCrucial;
import com.jemmy.framework.component.statistics.StatisticsCrucial;
import com.jemmy.framework.component.statistics.StatisticsEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * 虚拟订单
 */
@MappedSuperclass
@StatisticsEntity(crucial = { StatisticsCrucial.class, OrderUserCrucial.class })
@EntityListeners(AuditingEntityListener.class)
public class VirtualOrder extends Order {

}
