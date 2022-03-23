package com.jemmy.framework.controller;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface JpaRepository<T extends EntityKey> extends org.springframework.data.jpa.repository.JpaRepository<T, EntityKey> {
    T findByUuid(String uuid);

    Boolean existsByUuid(String uuid);

    @Transactional
    void deleteByUuid(String uuid);

    @Query(value = "select * from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where REFERENCED_TABLE_NAME = ?1", nativeQuery = true)
    List<Map<String, String>> getSqlInformation(String table_name);

    T findByUuidAndStatus(String uuid, Integer status);
}
