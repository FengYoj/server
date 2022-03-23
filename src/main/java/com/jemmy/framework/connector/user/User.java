package com.jemmy.framework.connector.user;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.api.annotation.WebFilter;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.annotation.field.Title;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.controller.EntityKey;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class User extends EntityKey {

    @TableAttr(sequence = 1)
    @CreateAttr(disable = true)
    @FieldAttr(value = "用户ID", search = true, unique = true)
    private String uid;

    @TableAttr(sequence = 2)
    @StepAttr(name = "info", title = "基本信息", sequence = 1)
    @Title
    @FieldAttr(value = "用户名", empty = false, search = true)
    private String username;

    @WebFilter
    @CreateAttr(type = FieldType.Password)
    @FieldAttr("密码")
    @TableAttr(disable = true)
    private String password;

}
