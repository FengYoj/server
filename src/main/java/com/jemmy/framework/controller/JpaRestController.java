package com.jemmy.framework.controller;

import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@DependsOn("SpringBeanUtils")
public @interface JpaRestController {
}
