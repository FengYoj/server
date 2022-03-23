package com.jemmy.framework.component.access;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.component.statistics.StatisticsEntity;
import com.jemmy.framework.controller.EntityKey;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
@StatisticsEntity(crucial = { AccessTypeCrucial.class, AccessUserCrucial.class, AccessTotalCrucial.class })
public class Access extends EntityKey {

    @FieldAttr(value = "类型", empty = false)
    @SelectField(fixed = { "wx-mp:微信小程序", "api:API接口" })
    private String type;

    @FieldAttr("IP")
    private String ip;

    @FieldAttr("路径")
    private String path;

    @FieldAttr("设备")
    @Column(length = 100000)
    private String agent;

    @FieldAttr("方法")
    private String method;

    @FieldAttr("类名")
    private String className;

    @FieldAttr("标题")
    private String title;

    @FieldAttr("访问用户ID")
    private String user;
}
