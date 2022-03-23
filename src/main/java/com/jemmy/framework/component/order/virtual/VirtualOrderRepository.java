package com.jemmy.framework.component.order.virtual;

import com.jemmy.framework.component.order.OrderRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface VirtualOrderRepository<E extends VirtualOrder> extends OrderRepository<E> {
}
