package com.jemmy.framework.data.sql;

import com.jemmy.framework.data.sql.entity.condition.Condition;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.ListPage;
import com.jemmy.framework.utils.SpringBeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.math.BigInteger;

public class Sql<E extends EntityKey> {

    private static final EntityManager entityManager = SpringBeanUtils.getBean(EntityManager.class);

    private final Class<E> entity;

    private final String table;

    public Sql(Class<E> entity) {
        this.entity = entity;
        this.table = EntityUtils.getTableName(entity);
    }

    public ListPage<E> page(Integer page, Integer size, Condition... conditions) {
        StringBuilder condition = new StringBuilder();

        for (Condition c : conditions) {
            if (condition.length() == 0) {
                condition.append(" where ");
            } else {
                condition.append(" and ");
            }

            condition.append(c.toString());
        }

        Query query = entityManager.createNativeQuery(String.format("select * from %s%s order by created_date desc limit %s, %s", table, condition.toString(), page, size), entity);
        Query total = entityManager.createNativeQuery(String.format("select count(uuid) from %s%s", table, condition.toString()));

        return new ListPage<E>(query.getResultList(), PageRequest.of(page, size, Sort.Direction.DESC, "createdDate"), ((BigInteger) total.getSingleResult()).longValue());
    }

    public Object sum(String count, Condition... conditions) {
        Query total = entityManager.createNativeQuery(String.format("select sum(%s) from %s%s", count, table, getCondition(conditions)));

        try {
            return total.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Object sum(String count, Integer day) {
        StringBuilder condition = new StringBuilder();

        if (day != null) {
            if (day.equals(0)) {
                condition.append(" where TO_DAYS(created_date) = TO_DAYS(NOW())");
            } else {
                condition.append(" where TO_DAYS(NOW()) - TO_DAYS(created_date) <= ").append(day);
            }
        }

        Query total = entityManager.createNativeQuery(String.format("select sum(%s) from %s%s", count, table, condition.toString()));

        try {
            return total.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ListPage<E> search(String match, String against, Integer page, Integer size, Condition... conditions) {
        StringBuilder condition = new StringBuilder();

        for (Condition c : conditions) {
            condition.append(" and ");
            condition.append(c.toString());
        }

        Query query = entityManager.createNativeQuery(String.format("SELECT * FROM %s WHERE MATCH (%s) AGAINST ('%s')%s limit %s, %s", table, match, against, condition.toString(), page, size), entity);
        Query totalQuery = entityManager.createNativeQuery(String.format("SELECT count(uuid) FROM %s WHERE MATCH (%s) AGAINST ('%s')%s limit %s, %s", table, match, against, condition.toString(), page, size));

        long total;

        try {
            total = ((BigInteger) totalQuery.getSingleResult()).longValue();
        } catch (NoResultException e) {
            total = 0L;
        }

        return new ListPage<E>(query.getResultList(), PageRequest.of(page, size, Sort.Direction.DESC, "createdDate"), total);
    }

    private String getCondition(Condition... conditions) {
        StringBuilder condition = new StringBuilder();

        for (Condition c : conditions) {
            if (condition.length() == 0) {
                condition.append(" where ");
            } else {
                condition.append(" and ");
            }

            condition.append(c.toString());
        }

        return condition.toString();
    }
}
