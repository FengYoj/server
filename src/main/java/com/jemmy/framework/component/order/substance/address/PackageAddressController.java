package com.jemmy.framework.component.order.substance.address;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.result.Result;

import java.util.List;

@AutoAPI
public class PackageAddressController extends JpaController<PackageAddress, PackageAddressRepository> {

    @Post
    public Result<String> save(PackageAddress entity) {
        return controller.save(entity);
    }

    @Get
    public Result<List<PackageAddress>> findAllByUser(@AutoParam User user) {
        return Result.<List<PackageAddress>>HTTP200().setData(repository.findAllByUser(user));
    }
}
