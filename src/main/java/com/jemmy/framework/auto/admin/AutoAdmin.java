package com.jemmy.framework.auto.admin;

import com.jemmy.framework.auto.page.operating.DefaultOperating;
import com.jemmy.framework.auto.page.operating.TableOperating;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@DependsOn("SpringBeanUtils")
public @interface AutoAdmin {
    String value() default "";

    boolean data() default true;

    Page pages() default Page.All;

    Class<? extends TableOperating> operating() default DefaultOperating.class;

    enum Page {
        Table("table"),
        All("table", "create", "edit");

        Page(String... pages) {
            this.name = pages;
        }

        private String[] name;

        public String[] getName() {
            return name;
        }

        public void setName(String[] name) {
            this.name = name;
        }
    }
}
