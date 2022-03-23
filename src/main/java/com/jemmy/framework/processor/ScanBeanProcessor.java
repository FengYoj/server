package com.jemmy.framework.processor;

import com.jemmy.framework.annotation.ScanBean;
import com.jemmy.framework.interfaces.ScanBeanMethod;
import com.jemmy.framework.interfaces.ScanBeanType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class ScanBeanProcessor implements BeanPostProcessor {

    private final List<ScanBeanMethod> classes;

    public ScanBeanProcessor() {
        // 获取扫描注解方法
        classes = ImportScanProcessor.getClasses();
        // 清空
        ImportScanProcessor.clearClasses();
    }

    /**
     * 初始化前的 Bean 处理事件
     * @param bean Bean
     * @param beanName Bean 名称
     */
    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        this.eachBean(bean, ScanBeanType.CLASS);

        for (Method method : bean.getClass().getDeclaredMethods()) {
            this.eachBean(method, ScanBeanType.METHOD);
        }

        return bean;
    }

    /**
     * 初始化后的 Bean 处理事件
     * @param bean Bean
     * @param beanName Bean 名称
     */
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        return bean;
    }

    /**
     * 遍历 Bean
     * @param bean Bean
     * @param type 类型
     */
    private void eachBean(Object bean, int type) {
        Class<?> clazz = bean.getClass();

        for (ScanBeanMethod scanBeanMethod : classes) {
            ScanBean scanBean = scanBeanMethod.getClass().getAnnotation(ScanBean.class);

            // 如检索注解，全文检索
            if (scanBean.type() != ScanBeanType.ANNOTATION && scanBean.type() != type) {
                continue;
            }

            if (scanBean.type() == ScanBeanType.ANNOTATION) {
                for (Class<?> a: scanBean.value()) {
                    if (a.isAnnotation() && AnnotationUtils.findAnnotation(clazz, (Class<? extends Annotation>) a) != null) {
                        boolean is = scanBeanMethod.check(bean);

                        if (is) {
                            scanBeanMethod.registrar(bean);
                        }
                    }
                }
            } else if (scanBean.type() == ScanBeanType.CLASS) {
                for (Class<?> a: scanBean.value()) {
                    if (bean.getClass().equals(a)) {
                        boolean is = scanBeanMethod.check(bean);

                        if (is) {
                            scanBeanMethod.registrar(bean);
                        }
                    }
                }
            }
        }
    }
}
