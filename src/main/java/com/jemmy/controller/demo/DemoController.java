package com.jemmy.controller.demo;

import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.auto.param.AutoParamType;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.result.Result;

@AutoAPI
public class DemoController extends JpaController<Demo, DemoRepository> {

    @AutoAdmin
    public static class admin extends Admin<Demo, DemoController> {}

    @Get
    public Result<String> getNameByUuid(@AutoParam(type = AutoParamType.ID_TO_ENTITY) Demo demo) {
        return Result.HTTP200(demo.getName());
    }
}
