package com.jemmy.framework.registrar;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.annotation.ScanBean;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.*;
import com.jemmy.framework.interfaces.ScanBeanMethod;
import com.jemmy.framework.interfaces.ScanBeanType;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.request.RequestMappingUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

@ScanBean(value = { AutoAPI.class, AutoAdmin.class }, type = ScanBeanType.ANNOTATION)
public class AutoAPIRegistrar implements ScanBeanMethod {

    @Override
    public boolean check(Object bean) {
        return true;
    }

    @Override
    public void registrar(Object bean) {
        AutoAPI autoAPI = AnnotationUtils.findAnnotation(bean.getClass(), AutoAPI.class);
        AutoAdmin autoAdmin = AnnotationUtils.findAnnotation(bean.getClass(), AutoAdmin.class);

        if (autoAPI == null && autoAdmin == null) {
            return;
        }

        String path;

        if (autoAPI != null && StringUtils.isExist(autoAPI.value())) {
            path = autoAPI.value();
        } else if (JpaController.class.isAssignableFrom(bean.getClass())) {
            path = ((JpaController<?, ?>) bean).getEntity().getSimpleName();
        } else if (Admin.class.isAssignableFrom(bean.getClass())) {
            path = ((Admin<?, ?>) bean).getEntity().getSimpleName();
        } else {
            path = bean.getClass().getSimpleName();
        }

        for (Method method : bean.getClass().getMethods()) {
            registrarWebAPI(bean, method, path);
        }
    }

    private void registrarWebAPI(Object bean, Method method, String path) {
        String name = StringUtils.upperCase(method.getName());

        Get get = AnnotationUtils.findAnnotation(method, Get.class);

        if (get != null) {

            name = StringUtils.defaults(get.value(), name);

            for (RequestPath p : get.path()) {
                this.registerMapping(bean, method, this.getPath(path, p.getPath()) + name, RequestMethod.GET);
            }

            return;
        }

        Post post = AnnotationUtils.findAnnotation(method, Post.class);

        if (post != null) {

            name = StringUtils.defaults(post.value(), name);

            for (RequestPath p : post.path()) {
                this.registerMapping(bean, method, this.getPath(path, p.getPath()) + name, RequestMethod.POST);
            }

            return;
        }

        Delete delete = AnnotationUtils.findAnnotation(method, Delete.class);

        if (delete != null) {

            name = StringUtils.defaults(delete.value(), name);

            for (RequestPath p : delete.path()) {
                this.registerMapping(bean, method, this.getPath(path, p.getPath()) + name, RequestMethod.GET, RequestMethod.DELETE);
            }

            return;
        }

        Api api = AnnotationUtils.findAnnotation(method, Api.class);

        if (api != null) {

            name = StringUtils.defaults(api.value(), name);

            for (RequestPath p : api.path()) {
                this.registerMapping(bean, method, this.getPath(path, p.getPath()) + name, api.method());
            }

        }
    }



    /**
     * 获取 API 路径
     * @param path 路径
     * @param name API 名称
     * @return API 路径
     */
    private String getPath(String path, String name) {
        return String.format("%s/%s/", name, path);
    }

    private void registerMapping(Object bean, Method method, String path, RequestMethod... requestMethod) {
        RequestMappingUtils.registerMapping(bean, method, path, requestMethod);
    }
}
