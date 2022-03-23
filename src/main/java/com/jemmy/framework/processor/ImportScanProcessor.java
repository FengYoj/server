package com.jemmy.framework.processor;

import com.jemmy.framework.interfaces.ScanBeanMethod;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ImportScanProcessor implements ImportBeanDefinitionRegistrar {

    private static List<ScanBeanMethod> classes = new ArrayList<>();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Class<?> clazz;

        try {
            clazz = Class.forName(importingClassMetadata.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (ScanBeanMethod.class.isAssignableFrom(clazz)) {
            try {
                classes.add((ScanBeanMethod) clazz.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<ScanBeanMethod> getClasses() {
        return classes;
    }

    public static void clearClasses() {
        classes = null;
    }
}
