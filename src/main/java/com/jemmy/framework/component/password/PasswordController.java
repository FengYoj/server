package com.jemmy.framework.component.password;

import com.jemmy.framework.component.password.history.HistoryPassword;
import com.jemmy.framework.component.password.history.HistoryPasswordController;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.MD5Utils;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.result.Result;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PasswordController extends JpaController<Password, PasswordRepository> {

    private final HistoryPasswordController historyPasswordController;

    public PasswordController(HistoryPasswordController historyPasswordController) {
        this.historyPasswordController = historyPasswordController;
    }

    /**
     * 创建密码
     * @param value 字符串密码
     * @return 密码实体
     */
    public Password create(String value) {
        Password password = new Password();
        // 盐
        String salt = MD5Utils.getMD5UUID();
        // 写入密码值
        password.setValue(MD5Utils.encrypt(value + salt));
        password.setType("MD5");
        password.setSalt(salt);
        // 保存
        controller.save(password);

        return password;
    }

    /**
     * 修改密码
     * @param password 密码实体
     * @param value 字符串密码
     */
    public void change(Password password, String value) {
        List<HistoryPassword> historyPasswords = password.getHistory();

        if (historyPasswords == null) {
            historyPasswords = new ArrayList<>();
        }

        Result<HistoryPassword> result = historyPasswordController.controller.callbackSave(new HistoryPassword(password.getModifiedDate(), password.getValue(), IpUtil.getIpAddr()));

        // 写入密码记录
        historyPasswords.add(result.get());

        // 写入新密码
        password.setValue(MD5Utils.encrypt(value + password.getSalt()));

        // 保存
        controller.save(password).throwout();
    }

    public Password onlySave(Password password) {
        if (StringUtils.isExist(password.getUuid())) {
            return onlyUpdate(password);
        }

        // 盐
        String salt = MD5Utils.getMD5UUID();
        // 写入密码值
        password.setValue(MD5Utils.encrypt(password.getValue() + salt));
        password.setType("MD5");
        password.setSalt(salt);

        return controller.onlySave(password);
    }

    public Password onlyUpdate(Password entity) {
        if (StringUtils.isExist(entity.getUuid())) {
            Result<Password> result = controller.findByUuid(entity.getUuid());

            if (result.isNormal()) {
                Password source = result.getData();

                // 修改密码后 MD5 加密
                if (!source.getValue().equals(entity.getValue())) {
                    entity.setValue(MD5Utils.encrypt(entity.getValue() + entity.getSalt()));
                }
            }

            return controller.onlySave(entity);
        }

        return controller.onlyUpdate(entity);
    }
}
