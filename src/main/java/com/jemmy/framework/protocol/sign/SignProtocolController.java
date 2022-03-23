package com.jemmy.framework.protocol.sign;

import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.protocol.Protocol;
import com.jemmy.framework.utils.result.Result;

public class SignProtocolController extends JpaController<SignProtocol, SignProtocolRepository> {

    public Result<?> sign(User user, Protocol protocol) {
        SignProtocol signProtocol = new SignProtocol();

        signProtocol.setUser(user);
        signProtocol.setProtocol(protocol);

        return controller.save(signProtocol).toEmpty();
    }

}
