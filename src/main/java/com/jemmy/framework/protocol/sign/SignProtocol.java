package com.jemmy.framework.protocol.sign;

import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.protocol.Protocol;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class SignProtocol extends EntityKey {

    @OneToOne
    private User user;

    @OneToOne
    private Protocol protocol;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}
