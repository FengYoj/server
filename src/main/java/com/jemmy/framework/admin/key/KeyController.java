package com.jemmy.framework.admin.key;

import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.UuidUtils;
import com.jemmy.framework.utils.result.Result;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeyController extends JpaController<Key, KeyRepository> {

    @AutoAPI
    public static class _ADMIN extends Admin<Key, KeyController> {
        @Post
        public Result<String> save(@AutoParam Key key) {
            // 写入 Key 值
            key.setPrivateKey(UuidUtils.getUUID32());
            // 保存
            return super.save(key);
        }
    }

    public Result<Key> getStatus(String privateKey) {
        return controller.examineFind(repository.findByPrivateKey(privateKey));
    }

}
