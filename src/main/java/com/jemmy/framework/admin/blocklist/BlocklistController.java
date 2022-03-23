package com.jemmy.framework.admin.blocklist;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.admin.controller.AdminAccountController;
import com.jemmy.framework.admin.dao.AdminAccount;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.controller.JpaRestController;
import com.jemmy.framework.utils.request.CookieUtils;
import com.jemmy.framework.utils.request.RequestUtils;
import com.jemmy.framework.utils.result.Result;

@JpaRestController
public class BlocklistController extends JpaController<Blocklist, BlocklistRepository> {

    @AutoAdmin
    public static class _ADMIN extends Admin<Blocklist, BlocklistController> {

        private final AdminAccountController adminAccountController;

        public _ADMIN(AdminAccountController adminAccountController) {
            this.adminAccountController = adminAccountController;
        }

        @Override
        @Post(path = RequestPath.ADMIN)
        public Result<String> save(@AutoParam Blocklist entity) {
            // 查找管理员数据
            Result<AdminAccount> result = adminAccountController.controller.findByUuid(CookieUtils.get(RequestUtils.getServlet(), "admin_id"));

            if (result.isBlank()) {
                return result.toObject();
            }

            entity.setCreator(result.getData().getUsername());

            return super.save(entity);
        }
    }

    public Boolean existsByIp(String ip) {
        return repository.existsByIp(ip);
    }
}
