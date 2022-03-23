package com.jemmy.framework.controller;

import com.jemmy.framework.component.statistics.StatisticsContinuation;
import com.jemmy.framework.controller.plusin.Export;
import com.jemmy.framework.registrar.ControllerRegistrar;
import com.jemmy.framework.utils.*;

public class JpaController<E extends EntityKey, R extends JpaRepository<E>> {

    protected R repository;

    protected Class<E> entity;

    protected final StatisticsContinuation<E> statistics;

    public final Controller<E, R> controller;

    public final Export export;

    public JpaController() {
        entity = ClassUtils.getGeneric(this.getClass(), 0);
        repository = ClassUtils.getBean(this.getClass(), 1);
        statistics = new StatisticsContinuation<>(entity);
        controller = new Controller<>(entity, repository, statistics);
        export = new Export(controller);

        // 添加至注册器
        ControllerRegistrar.add(entity, this);
    }

    /**
     * 获取实体
     * @return 实体
     */
    public Class<E> getEntity() {
        return entity;
    }
}

