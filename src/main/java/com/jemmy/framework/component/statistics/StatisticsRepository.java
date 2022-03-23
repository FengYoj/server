package com.jemmy.framework.component.statistics;

import com.jemmy.framework.controller.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics> {

    @Query(value = "select DATE_FORMAT(created_date, \"%Y-%m-%d\") AS time, sum(value) AS total from statistics where name = ?1 and type = 0 and crucial is null and DATE_SUB(CURDATE(), INTERVAL ?2 DAY) <= date(created_date) GROUP BY DATE_FORMAT(created_date, \"%Y-%m-%d\")", nativeQuery = true)
    List<Object[]> findStatistics(String name, Integer day);

    @Query(value = "select DATE_FORMAT(created_date, \"%Y-%m-%d\") AS time, sum(value) AS total from statistics where name = ?1 and type = 0 and crucial = ?3 and DATE_SUB(CURDATE(), INTERVAL ?2 DAY) <= date(created_date) GROUP BY DATE_FORMAT(created_date, \"%Y-%m-%d\")", nativeQuery = true)
    List<Object[]> findStatistics(String name, Integer day, String crucial);

    @Query("select s from Statistics s where s.name = ?1 and s.field = ?2 and s.type = 1")
    Statistics findByNameAndField(String name, String field);

    @Query("select s from Statistics s where s.name = ?1 and s.field = ?2 and s.crucial = ?3 and s.type = 1")
    Statistics findByNameAndFieldAndCrucial(String name, String field, String crucial);

    @Query("select s from Statistics s where s.name = ?1 and s.type = 1 and s.crucial is null")
    Statistics findByName(String name);

    @Query("select s from Statistics s where s.name = ?1 and s.crucial = ?2 and s.type = 1")
    Statistics findByNameAndCrucial(String name, String crucial);

    @Query(value = "select * from statistics where name = ?1 and type = 0 and crucial is null and TO_DAYS(created_date) = TO_DAYS(NOW())", nativeQuery = true)
    Statistics findByNameAndToday(String name);

    @Query(value = "select * from statistics where name = ?1 and crucial = ?2 and type = 0 and TO_DAYS(created_date) = TO_DAYS(NOW())", nativeQuery = true)
    Statistics findByNameAndCrucialAndToday(String name, String crucial);

    List<Statistics> findByNameAndFieldIn(String name, List<String> field);

    List<Statistics> findByNameAndFieldInAndCrucial(String name, List<String> field, String crucial);

    @Query(value = "select * from statistics where name = ?1 and DATE_SUB(CURDATE(), INTERVAL ?2 DAY) = date(created_date) GROUP BY DATE_FORMAT(created_date, \"%Y-%m-%d\")", nativeQuery = true)
    Statistics findByDayBefore(String name, Integer day);

    @Query(value = "select * from statistics where name = ?1 and crucial = ?3 and DATE_SUB(CURDATE(), INTERVAL ?2 DAY) = date(created_date) GROUP BY DATE_FORMAT(created_date, \"%Y-%m-%d\")", nativeQuery = true)
    Statistics findByDayBefore(String name, Integer day, String crucial);
}
