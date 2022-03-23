package com.jemmy.framework.auto.page.type.password;

import lombok.Data;

@Data
public class PasswordConfig {

    private boolean encryption = true;

    private boolean entity;

    public PasswordConfig() {
    }

    public PasswordConfig(PasswordField field) {
        encryption = field.encryption();
    }

}
