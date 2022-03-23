package com.jemmy.framework.component.order.substance.postage;

import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.controller.JpaController;

@AutoAPI
public class PostageController extends JpaController<Postage, PostageRepository> {

    @AutoAdmin
    public static class _ADMIN extends Admin<Postage, PostageController> {}

}
