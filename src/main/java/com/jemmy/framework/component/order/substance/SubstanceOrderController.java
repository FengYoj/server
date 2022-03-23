package com.jemmy.framework.component.order.substance;

import com.jemmy.framework.component.order.OrderChannel;
import com.jemmy.framework.component.order.OrderController;

public class SubstanceOrderController<E extends SubstanceOrder, R extends SubstanceOrderRepository<E>> extends OrderController<E, R> {

    public SubstanceOrderController(String prefix, OrderChannel channel) {
        super(prefix, channel);
    }

}
