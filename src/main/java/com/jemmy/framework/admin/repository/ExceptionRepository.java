package com.jemmy.framework.admin.repository;

import com.jemmy.framework.admin.dao.Exception;
import com.jemmy.framework.controller.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExceptionRepository extends JpaRepository<Exception> {

    @Query(value = "insert into exception values(?1)", nativeQuery = true)
    Exception save();

    @Query(value = "select COUNT(uuid) AS total from exception where TO_DAYS(created_date) = TO_DAYS(NOW())", nativeQuery = true)
    Integer findTodayNumber();

    @Query(value = "select COUNT(uuid) AS total from exception where TO_DAYS(NOW()) - TO_DAYS(created_date) <= 1", nativeQuery = true)
    Integer findYesterdayNumber();
}
