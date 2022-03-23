package com.jemmy.framework.annotation;

import com.jemmy.framework.interfaces.ScanBeanType;
import com.jemmy.framework.processor.ImportScanProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Import(ImportScanProcessor.class)
public @interface ScanBean {
    Class<? extends Object>[] value();

    int type() default ScanBeanType.CLASS;
}
