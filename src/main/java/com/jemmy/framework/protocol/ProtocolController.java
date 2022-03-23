package com.jemmy.framework.protocol;

import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.admin.AutoMenu;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.component.access.Access;
import com.jemmy.framework.component.access.AccessController;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.result.Result;

@AutoAPI
public class ProtocolController extends JpaController<Protocol, ProtocolRepository> {

    @AutoAdmin
    @AutoMenu(title = "协议")
    public static class _ADMIN extends Admin<Protocol, ProtocolController> {}

    public Result<Protocol> findByIdentifier(String identifier) {
        return Result.data(repository.findByIdentifier(identifier));
    }
}
