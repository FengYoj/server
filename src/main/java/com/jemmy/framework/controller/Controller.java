package com.jemmy.framework.controller;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.component.statistics.StatisticsContinuation;
import com.jemmy.framework.controller.plusin.Export;
import com.jemmy.framework.data.sql.entity.condition.Condition;
import com.jemmy.framework.object.Filter;
import com.jemmy.framework.utils.*;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller<E extends EntityKey, R extends JpaRepository<E>> extends QuerySentence<E> {

    protected final EntityManager entityManager = SpringBeanUtils.getBean(EntityManager.class);

    private List<String> searchField;

    private final StatisticsContinuation<E> statistics;

    protected final R repository;

    protected final Class<E> entity;

    public Controller(Class<E> entity, R repository, StatisticsContinuation<E> statistics) {
        super(entity);

        this.entity = entity;
        this.repository = repository;
        this.statistics = statistics;

        // 更新数据表，写入搜索字段
        this.updateTable();
    }

    public Result<List<String>> saveAll(List<E> es) {
        List<E> list = repository.saveAll(es);
        List<String> res = new ArrayList<>();

        for (E e : list) {
            res.add(e.getUuid());
        }

        return Result.<List<String>>HTTP200().setData(res);
    }

    /**
     * 检查实体并保存
     * @param entity 实体
     * @param bindingResult 事务
     * @return 实体
     */
    public Result<String> save(E entity, BindingResult bindingResult) {
        if (ErrorUtils.hasErrors(bindingResult)) {
            return ErrorUtils.getStatus(bindingResult);
        }

        return save(entity);
    }

    public Result<String> save(E entity) {
        String uuid = entity.getUuid();

        if (!StringUtils.isBlank(uuid)) {
            return update(uuid, entity);
        }

        Result<E> result = this.checkSave(entity);

        if (result.isBlank()) {
            return result.toObject();
        }

        // 统计
        statistics.add(entity);

        return Result.<String>HTTP200().setData(result.getData().getUuid());
    }

    public E onlySave(E entity) {

        // 统计
        statistics.add(entity);

        return repository.save(entity);
    }

    public E onlyUpdate(E entity) {
        if (StringUtils.isBlank(entity.getUuid())) {
            return onlySave(entity);
        }

        Result<E> result = this.findByUuid(entity.getUuid());

        if (result.isBlank()) {
            return onlySave(entity);
        }

        return onlySave(EntityUtils.assign(result.getData(), entity));
    }

    public E onlySave(Object entity) {
        return repository.saveAndFlush((E) entity);
    }

    public Result<E> callbackSave(E entity) {
        return ParamUtils.callbackSave(entity);
    }

    public Result<String> save(E entity, String id, BindingResult bindingResult) {
        if (ErrorUtils.hasErrors(bindingResult)) {
            return ErrorUtils.getStatus(bindingResult);
        }

        return save(entity, id);
    }

    public Result<String> save(E entity, String id) {
        if (StringUtils.isBlank(id)) {
            return save(entity);
        }

        Field field = AnnotationUtils.getField(entity.getClass(), FieldAttr.class);

        if (field == null) {
            return new Result<String>(ResultCode.HTTP400).putMessage("No corresponding linked list");
        }

        // 获取注解
        FieldAttr entityController = field.getAnnotation(FieldAttr.class);

        // JPA 控制器
        JpaController<?, ?> jpaController = SpringBeanUtils.getBean(entityController.controller());

        // 根据 uuid 获取实体
        Result<?> result = jpaController.controller.findByUuid(id);

        if (result.getStatus() != 200) {
            return result.toObject();
        }

        // 设置属性
        EntityUtils.setAttribute(entity, field.getName(), result.getData());

        return save(entity);
    }

    public Result<String> update(E entity) {
        return this.update(entity.getUuid(), entity);
    }

    public Result<String> update(String uuid, E entity) {
        E e = repository.findByUuid(uuid);

        if (e == null) {
            return Result.of(ResultCode.HTTP404);
        }

//        entity.setId(e.getId());
        entity.setUuid(uuid);

        if (entity.getStatus() == null) {
            entity.setStatus(e.getStatus());
        }

        return ParamUtils.save(entity);
    }

    public Result<E> callbackUpdate(String uuid, E entity) {
        E e = repository.findByUuid(uuid);

        if (e == null) {
            return Result.of(ResultCode.HTTP404);
        }

//        entity.setId(e.getId());
        entity.setUuid(uuid);

        if (entity.getStatus() == null) {
            entity.setStatus(e.getStatus());
        }

        repository.save(entity);

        return Result.<E>of(ResultCode.HTTP200).setData(entity);
    }

    /**
     * 根据 uuid 查找内容
     * @param uuid uuid
     * @return 实体
     */
    public Result<E> findByUuid(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return Result.<E>of(ResultCode.HTTP400).putMessage("uuid cannot be empty");
        }

        return examineFind(repository.findByUuid(uuid));
    }

    public Result<E> examineFind(E entity) {
        if (entity == null) {
            return Result.of(ResultCode.HTTP404);
        }
        return Result.<E>of(ResultCode.HTTP200).setData(entity);
    }

    public Result<Page<E>> findAllToPage(Integer page, Integer size) {
        Page<E> pageEntity = repository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, "createdDate"));

        if (!pageEntity.hasContent() && page > 1) {
            pageEntity = repository.findAll(PageRequest.of(0, size, Sort.Direction.DESC, "createdDate"));
        }

        return Result.<Page<E>>of(ResultCode.HTTP200).setData(pageEntity);
    }

    public Result<List<E>> findAll() {
        List<E> list = repository.findAll();

        return Result.<List<E>>of(ResultCode.HTTP200).setData(list);
    }

    public Result<Object> deleteAndUpdateByUuid(String deleteId, String updateId) {

        for (Map<String, String> item : repository.getSqlInformation(getTableName())) {
            String sql = "update " + item.get("TABLE_NAME") + " SET " + item.get("COLUMN_NAME") + " = '" + updateId + "' where " + item.get("COLUMN_NAME") + " = '" + deleteId + "'";

            entityManager.createNativeQuery(sql);
        }

        try {
            repository.deleteByUuid(deleteId);
        } catch (Exception e) {
            return Result.of(ResultCode.HTTP400).putMessage(e.getMessage());
        }

        return Result.of(ResultCode.HTTP200);
    }

    public Result<?> hideByUuid(String uuid) {
        Result<E> result = findByUuid(uuid);

        if (result.isBlank()) {
            return result.toObject();
        }

        return this.hide(result.getData());
    }

    public Result<?> hide(E entity) {
        entity.setStatus(4);

        return this.save(entity).setData(null);
    }

    public Result<?> delete(E entity) {
        // 查找并置空所有关联的记录
        for (Map<String, String> item : repository.getSqlInformation(StringUtils.toUnderline(this.entity.getSimpleName()))) {
            try {
                super.update(item.get("TABLE_NAME"), item.get("COLUMN_NAME"), null, String.format("%s = '%s'", item.get("COLUMN_NAME"), entity.getUuid()));
            } catch (Exception ignored) {}
        }

        try {
            repository.delete(entity);
        } catch (Exception e) {
            return Result.of(ResultCode.HTTP400).putMessage(e.getMessage());
        }

        return Result.of(ResultCode.HTTP200);
    }

    public Result<?> deleteByUuid(String uuid) {
        Result<E> result = findByUuid(uuid);

        if (result.isBlank()) {
            return result.toObject();
        }

        return this.delete(result.getData());
    }

    public Result<Boolean> existsByUuid(String uuid) {
        return Result.<Boolean>of(ResultCode.HTTP200).setData(repository.existsByUuid(uuid));
    }

    public Result<List<E>> findCustomizes(String key, Object val) {
        return findCustomizes(key, val, false);
    }

    public Result<List<E>> findCustomizes(String key, Object val, boolean isFuzzy) {
        // 值为实体类时，查询主键
        if (val instanceof EntityKey) {
            val = ((EntityKey) val).getUuid();
        }

        StringBuilder sb = new StringBuilder();

        sb.append("select * from ").append(getTableName()).append(" where ").append(StringUtils.toUnderline(key));

        if (isFuzzy) {
            sb.append(" like ").append(val instanceof String ? ("\"%" + val +"%\"") : "%" + val + "%");
        } else {
            sb.append(" = ").append(val instanceof String ? ("\"" + val +"\"") : val);
        }

        Query query = entityManager.createNativeQuery(sb.toString(), this.entity);

        try {
            List<E> list = (List<E>) query.getResultList();

            if (list.size() > 0) {
                return Result.<List<E>>of(ResultCode.HTTP200).setData(list);
            }
        } catch (Exception ignored) { }

        return Result.of(ResultCode.HTTP404);
    }

    public Result<E> findCustomize(String key, Object val) {
        return this.findCustomize(key, val, false);
    }

    public Result<E> findCustomize(String key, Object val, boolean isFuzzy) {
        Result<List<E>> result = findCustomizes(key, val, isFuzzy);

        if (result.isNormal()) {
            return Result.<E>of(ResultCode.HTTP200).setData(result.getData().get(0));
        }

        return result.toObject();
    }

    public Result<E> findCustomize(List<Filter> filter) {
        Result<List<E>> result = findCustomizes(filter);

        if (result.isNormal()) {
            return Result.<E>of(ResultCode.HTTP200).setData(result.getData().get(0));
        }

        return result.toObject();
    }

    public Result<List<E>> findCustomizes(List<Filter> filter) {
        return findCustomizes(filter, false);
    }

    public Result<List<E>> findCustomizes(List<Filter> filter, boolean isFuzzy) {
        StringBuilder sb = new StringBuilder();

        for (var i = 0; i < filter.size(); i++) {
            Filter where = filter.get(i);

            if (i > 0) {
                sb.append(" and ");
            }

            Object val = where.getValue();

            sb.append(StringUtils.toUnderline(where.getKey()));

            // 值为实体类时，查询主键
            if (val instanceof EntityKey) {
                val = ((EntityKey) val).getUuid();
            }

            if (val instanceof String) {
                if (isFuzzy) {
                    sb.append(" like ").append("\"%").append(val).append("%\"");
                } else {
                    sb.append(" = ").append("\"").append(val).append("\"");
                }
            } else {
                if (isFuzzy) {
                    sb.append(" like ").append("%").append(val).append("%");
                } else {
                    sb.append(" = ").append(val);
                }
            }
        }

        return findCustomize(sb.toString());
    }

    public Result<List<E>> findCustomize(String filter) {
        Query query = entityManager.createNativeQuery("select * from " + getTableName() + " where " + filter, this.entity);

        try {
            List<E> list = (List<E>) query.getResultList();

            if (list.size() > 0) {
                return Result.<List<E>>of(ResultCode.HTTP200).setData(list);
            }
        } catch (Exception ignored) {}

        return Result.of(ResultCode.HTTP404);
    }

    /**
     * 数据库查询
     * @param sql sql语句
     * @return 结果
     */
    public List<?> query(String sql) {
        Query query = entityManager.createNativeQuery(sql);

        // 返回结果列表
        return query.getResultList();
    }

    /**
     * 搜索，返回 Page 实体
     * @param page 当前页面
     * @param size 页面长度
     * @param search 搜索文本
     * @return Page
     */
    public Result<Page<E>> searchToPage(Integer page, Integer size, String search, Condition... conditions) {
        if (searchField.size() <= 0) {
            return Result.<Page<E>>of(ResultCode.HTTP500).setMessage("当前数据表尚未开启搜索模块");
        }

        return new Result<Page<E>>(ResultCode.HTTP200).setData(super.searchToPage(searchField, search, page, size, conditions));
    }

    /**
     * 搜索，返回实体列表
     * @param search 搜索文本
     * @return List
     */
    public Result<List<E>> search(String search) {
        if (searchField.size() <= 0) {
            return Result.<List<E>>of(ResultCode.HTTP500).setMessage("当前数据表尚未开启搜索模块");
        }

        return Result.<List<E>>of(ResultCode.HTTP200).setData(super.search(searchField, search));
    }

    /**
     * 搜索，返回 Page 实体
     * @param page 当前页面
     * @param limit 页面长度
     * @param search 搜索文本
     * @return Page
     */
    public Result<Page<E>> searchToPage(Integer page, Integer limit, String search, List<Filter> filter) {
        if (searchField.size() <= 0) {
            return Result.<Page<E>>of(ResultCode.HTTP500).setMessage("当前数据表尚未开启搜索模块");
        }

        List<E> searchResults = super.searchAndWheres(searchField, search, filter);

        return new Result<Page<E>>(ResultCode.HTTP200).setData(new ListPage<>(searchResults, PageRequest.of(page, limit, Sort.Direction.DESC, "createdDate")));
    }

    /**
     * 查找所有过滤数据
     * @param page 页码
     * @param limit 条数
     * @param filter 过滤参数
     * @return Page 实体
     */
    public Result<Page<E>> findAllByFilterToPage(Integer page, Integer limit, List<Filter> filter) {
        Result<List<E>> result = findCustomizes(filter);

        if (result.isBlank()) {
            return Result.<Page<E>>HTTP200().setData(new ListPage<>());
        }

        Page<E> pages = new ListPage<>(result.getData(), PageRequest.of(page, limit, Sort.Direction.DESC, "createdDate"));

        return Result.<Page<E>>of(ResultCode.HTTP200).setData(pages);
    }

    /**
     * Web -> 查找实体，状态值需为 1
     * @param uuid id
     * @return 实体
     */
    public Result<E> findByUuidToWeb(String uuid) {
        return this.examineFind(repository.findByUuidAndStatus(uuid, 1));
    }

    /**
     * 获取实体
     * @return 实体
     */
    public Class<E> getEntity() {
        return entity;
    }

    private void updateTable() {
        List<String> fields = new ArrayList<>();

        for (Field field : EntityUtils.getFields(entity)) {
            if (field.isAnnotationPresent(FieldAttr.class) && field.getAnnotation(FieldAttr.class).search()) {
                fields.add(StringUtils.toUnderline(field.getName()));
            }
        }

        if (fields.size() > 0) {
            super.alterIndex("search", "FULLTEXT", String.join(",", fields));
        }

        searchField = fields;
    }

    /**
     * 获取数据表名称
     * @return 表名
     */
    private String getTableName() {
        return EntityUtils.getTableName(entity);
    }

    private Result<E> checkSave(E e) {
        // 保存实体
        return ParamUtils.callbackSave(e);
    }
}
