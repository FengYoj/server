package com.jemmy.framework.auto.page.type.password;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PasswordField {

    boolean encryption() default true;

}
