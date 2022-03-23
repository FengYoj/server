package com.jemmy.framework.component.password;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jemmy.framework.component.password.history.HistoryPassword;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.registrar.ControllerRegistrar;
import com.jemmy.framework.utils.MD5Utils;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Password extends EntityKey {

    private String value;

    @JsonIgnore
    private String type;

    @JsonIgnore
    private String salt;

    @JsonIgnore
    @OneToMany
    private List<HistoryPassword> history;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<HistoryPassword> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryPassword> history) {
        this.history = history;
    }

    /**
     * 校验是否匹配
     * @param password MD5 密码
     * @return 匹配状态
     */
    @JsonIgnore
    public boolean isMatch(String password) {
        return MD5Utils.encrypt(password + salt).equals(value);
    }

    public void change(String value) {
        var controller = (PasswordController) this.getController();
        // 修改密码
        controller.change(this, value);
    }

    /**
     * 创建密码实体
     * @param password 字符串密码
     * @return 密码实体
     */
    @JsonIgnore
    public static Password create(String password) {
        var controller = (PasswordController) ControllerRegistrar.get(Password.class);

        return controller.create(password);
    }
}
