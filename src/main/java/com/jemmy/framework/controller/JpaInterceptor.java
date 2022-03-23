package com.jemmy.framework.controller;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class JpaInterceptor implements StatementInspector {
    @Override
    public String inspect(String s) {
        System.out.println(s);

        return s;
    }
}
