package com.jemmy.framework.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.registrar.ControllerRegistrar;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class EntityKey {

//    @Field
//    @Column(name = "id", columnDefinition = "bigint(20) not null UNIQUE key auto_increment")
//    @TableAttr(width = 60, sort = true, sequence = 99)
//    @FieldAttr("ID")
//    @CreateAttr(display = false)
//    private Long id;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "uuid", length = 32)
    @TableAttr(disable = true)
    @CreateAttr(disable = true)
    @FieldAttr(value = "UUID", search = true)
    private String uuid;

    @CreateAttr(disable = true)
    @Column(updatable=false)
    @CreatedDate
    @FieldAttr("创建日期")
    @TableAttr(width = 210)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @CreateAttr(disable = true)
    @LastModifiedDate
    @FieldAttr("修改日期")
    @TableAttr(width = 210)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date modifiedDate;

    @CreateAttr(disable = true)
    @FieldAttr(value = "状态值", filter = true)
    @SelectField(fixed = { "0:未知", "1:正常", "3:禁用", "4:已删除" })
    private Integer status = 1;

    public void setStatus(Integer status) {
        this.status = status == null ? 1 : status;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setCreatedDate(Date date) {
        this.createdDate = date;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setModifiedDate(Date date) {
        this.modifiedDate = date;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public Integer getStatus() {
        return status;
    }

    @JsonIgnore
    public <C extends JpaController<?, ?>> C getController() {
        return ControllerRegistrar.get(this.getClass());
    }
}
