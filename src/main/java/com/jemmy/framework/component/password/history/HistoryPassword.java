package com.jemmy.framework.component.password.history;

import com.jemmy.framework.controller.EntityKey;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

@Data
@Entity
public class HistoryPassword extends EntityKey {

    private Date createDate;

    private String value;

    private String ip;

    public HistoryPassword() {
    }

    public HistoryPassword(Date createDate, String value, String ip) {
        this.createDate = createDate;
        this.value = value;
        this.ip = ip;
    }
}
