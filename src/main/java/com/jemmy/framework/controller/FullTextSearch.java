package com.jemmy.framework.controller;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

@Component
public class FullTextSearch<T> {
    private String matching;
    private String[] onFields;

    private Class<T> forEntity;

    private final EntityManager entityManager;

    @Autowired
    public FullTextSearch(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public FullTextSearch<T> createSearch(Class<T> aClass) {
        this.forEntity = aClass;
        return this;
    }

    public FullTextSearch<T> setMatching(String text) {
        this.matching = text;
        return this;
    }

    public FullTextSearch<T> setOnFields(String... onFields) {
        this.onFields = onFields;
        return this;
    }

    public List<T> getQueryData() {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(forEntity)
                .get();

        // a very basic query by keywords
        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .onFields(onFields)
                .matching(matching)
                .createQuery();

        // wrap Lucene query in an Hibernate Query object
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, forEntity);

        return (List<T>) jpaQuery.getResultList();
    }
}
