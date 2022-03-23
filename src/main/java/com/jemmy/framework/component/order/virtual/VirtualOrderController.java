package com.jemmy.framework.component.order.virtual;

import com.jemmy.framework.component.order.Order;
import com.jemmy.framework.component.order.OrderChannel;
import com.jemmy.framework.component.order.OrderController;
import com.jemmy.framework.component.order.OrderRepository;

public class VirtualOrderController<E extends VirtualOrder, R extends VirtualOrderRepository<E>> extends OrderController<E, R> {

    public VirtualOrderController(String prefix, OrderChannel channel) {
        super(prefix, channel);
    }

}
