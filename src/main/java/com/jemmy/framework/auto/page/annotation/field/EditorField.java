package com.jemmy.framework.auto.page.annotation.field;

import javax.persistence.Column;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EditorField {
}
