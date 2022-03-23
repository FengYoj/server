package com.jemmy.framework.admin.dao;

import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.api.annotation.WebFilter;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.component.password.Password;
import com.jemmy.framework.component.resources.ResourceAttr;
import com.jemmy.framework.component.resources.image.ResourceImage;
import com.jemmy.framework.controller.EntityKey;
import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.search.annotations.Field;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@EntityAttr(value = "管理员")
public class AdminAccount extends EntityKey {

    @FieldAttr("头像")
    @ResourceAttr
    @OneToOne
    private ResourceImage avatar;

    @Field
    @FieldAttr(value = "用户名", empty = false, search = true, unique = true)
    private String username;

    @WebFilter
    @FieldAttr(value = "密码", empty = false)
    @TableAttr(disable = true)
    @OneToOne
    private Password password;

    @NotNull
    @FieldAttr(value = "用户等级", empty = false)
    @SelectField(fixed = {"超级用户", "普通用户"})
    @StepAttr(name = "grade", title = "选择用户等级")
    private Integer grade;

    @FieldAttr(value = "描述", search = true)
    private String description;

    @FieldAttr(value = "电子邮箱", search = true)
    private String email;

    public ResourceImage getAvatar() {
        return avatar;
    }

    public void setAvatar(ResourceImage avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
