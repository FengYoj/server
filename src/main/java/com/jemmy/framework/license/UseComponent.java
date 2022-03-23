package com.jemmy.framework.license;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@UseModule
public @interface UseComponent {
}
