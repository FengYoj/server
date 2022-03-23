package com.jemmy.framework.component.order.substance;

import com.jemmy.framework.component.order.OrderRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SubstanceOrderRepository<E extends SubstanceOrder> extends OrderRepository<E> {
}
