package com.jemmy.framework.admin.ordersException;

import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.controller.JpaController;

@AutoAPI
public class OrdersExceptionController extends JpaController<OrdersException, OrdersExceptionRepository> {

    @AutoAdmin(pages = AutoAdmin.Page.Table)
    public static class _ADMIN extends Admin<OrdersException, OrdersExceptionController> {}

}
