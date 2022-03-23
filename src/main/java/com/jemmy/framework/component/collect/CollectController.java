package com.jemmy.framework.component.collect;

import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.result.Result;

@AutoAPI
public class CollectController extends JpaController<Collect, CollectRepository> {

    @AutoAdmin(pages = AutoAdmin.Page.Table, operating = CollectOperating.class)
    public static class _ADMIN extends Admin<Collect, CollectController> {}

    @Post
    public Result<String> save(@AutoParam Collect collect) {
        return controller.save(collect);
    }
}
