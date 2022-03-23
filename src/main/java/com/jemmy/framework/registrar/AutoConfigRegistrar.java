package com.jemmy.framework.registrar;

import com.jemmy.framework.annotation.ScanBean;
import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.ConfigProcessor;
import com.jemmy.framework.interfaces.ScanBeanMethod;
import com.jemmy.framework.interfaces.ScanBeanType;

@ScanBean(value = AutoConfig.class, type = ScanBeanType.ANNOTATION)
public class AutoConfigRegistrar implements ScanBeanMethod {

    @Override
    public boolean check(Object bean) {
        return true;
    }

    @Override
    public void registrar(Object bean) {
        ConfigProcessor.add(bean);
    }
}
