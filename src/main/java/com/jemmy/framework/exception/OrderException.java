package com.jemmy.framework.exception;

import com.jemmy.framework.admin.ordersException.OrdersException;
import com.jemmy.framework.component.order.Order;

public class OrderException extends Exception {

    private final OrdersException exception = new OrdersException();

    public OrderException(Order order, String message, String paymentPlatform) {
        exception.setOrderId(order.getUuid());
        exception.setPaymentStatus(order.getPaymentStatus());
        exception.setMessage(message);
        exception.setPaymentPlatform(paymentPlatform);
    }

    public OrderException(String message, String paymentPlatform) {
        exception.setMessage(message);
        exception.setPaymentPlatform(paymentPlatform);
    }

    public OrderException(String message) {
        exception.setMessage(message);
    }

    public OrdersException getException() {
        return exception;
    }

    public static OrderException wxPay(Order order, String message) {
        return new OrderException(order, message, "wx");
    }

    public static OrderException wxPay(String message) {
        return new OrderException(message, "wx");
    }
}
