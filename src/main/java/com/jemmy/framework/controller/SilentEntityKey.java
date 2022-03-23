package com.jemmy.framework.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class SilentEntityKey extends EntityKey {

    @Override
    @JsonIgnore
    public String getUuid() {
        return super.getUuid();
    }

    @Override
    @JsonIgnore
    public Date getCreatedDate() {
        return super.getCreatedDate();
    }

    @Override
    @JsonIgnore
    public Date getModifiedDate() {
        return super.getModifiedDate();
    }

    @Override
    @JsonIgnore
    public Integer getStatus() {
        return super.getStatus();
    }
}
