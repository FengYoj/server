package com.jemmy.framework.controller;

import com.jemmy.framework.data.sql.Sql;
import com.jemmy.framework.data.sql.entity.condition.Condition;
import com.jemmy.framework.object.Filter;
import com.jemmy.framework.utils.ClassUtils;
import com.jemmy.framework.utils.ListPage;
import com.jemmy.framework.utils.SpringBeanUtils;
import com.jemmy.framework.utils.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class QuerySentence<T extends EntityKey> {

    private final EntityManager entityManager = SpringBeanUtils.getBean(EntityManager.class);

    private final PlatformTransactionManager transactionManager = SpringBeanUtils.getBean(PlatformTransactionManager.class);

    // 数据表名称
    private final String table;

    private final Class<T> entity;

    private final Sql<T> sql;

    public QuerySentence(Class<T> entity) {
        String table = null;

        // 判断是否存在自定义数据表名称
        if (entity.isAnnotationPresent(Entity.class)) {
            table = entity.getAnnotation(Entity.class).name();
        }

        // 无自定义表名则使用默认表名
        if (StringUtils.isBlank(table)) {
            table = StringUtils.toUnderline(entity.getSimpleName());
        }

        this.table = table;
        this.entity = entity;
        this.sql = new Sql<>(entity);
    }

    /**
     * 查找字段组（字段列中的非重复值）
     * @param field 字段名
     * @return 列表
     */
    public List<?> findFieldGroup(String field) {
        Query query = entityManager.createNativeQuery(String.format("select %s from %s group by '%s'", field, table, field));

        return query.getResultList();
    }

    public void alterIndex(String name, String index, String field) {
        var transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                List<?> list = entityManager.createNativeQuery(String.format("SELECT 1 IndexIsThere FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema=DATABASE() AND table_name='%s' AND index_name='%s' limit 1", table, name)).getResultList();

                entityManager.createNativeQuery("ALTER TABLE " + table + (list == null || list.size() <= 0 ? " " : String.format(" DROP INDEX %s, ", name)) + String.format("ADD %s INDEX %s(%s)", index, name, field)).executeUpdate();
            }
        });
    }

    public void update(String name, String field, String value, String where) {
        var transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                entityManager.createNativeQuery(String.format("update %s set %s = %s where %s", name, field, value, where)).executeUpdate();
            }
        });
    }

    public List<T> search(List<String> fields, String content) {
        Query query = entityManager.createNativeQuery(String.format("SELECT * FROM %s WHERE MATCH (%s) AGAINST ('%s');", table, String.join(",", fields), content), entity);

        return (List<T>) query.getResultList();
    }

    public ListPage<T> searchToPage(List<String> fields, String content, Integer page, Integer size, Condition... conditions) {
        return this.sql.search(String.join(",", fields), content, page, size, conditions);
    }

    public List<T> searchAndWheres(List<String> fields, String content, List<Filter> wheres) {
        Query query = entityManager.createNativeQuery(String.format("SELECT * FROM %s WHERE MATCH (%s) AGAINST ('%s') AND %s;", table, String.join(",", fields), content, getWhere(wheres)), entity);

        try {
            return (List<T>) query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    private String getWhere(List<Filter> wheres) {
        StringBuilder sb = new StringBuilder();

        for (var i = 0; i < wheres.size(); i++) {
            Filter where = wheres.get(i);

            if (i > 0) {
                sb.append(" and ");
            }

            Object value = where.getValue();

            if (value instanceof String) {
                value = "'" + value + "'";
            }

            sb.append(String.format("%s = %s", where.getKey(), value));
        }

        return sb.toString();
    }
}
