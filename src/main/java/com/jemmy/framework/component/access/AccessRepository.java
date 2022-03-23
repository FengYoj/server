package com.jemmy.framework.component.access;

import com.jemmy.framework.controller.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AccessRepository extends JpaRepository<Access> {

    @Query(value = "select DATE_FORMAT(created_date, \"%Y-%m-%d\") AS time, COUNT(uuid) AS total from access where type = ?1 and DATE_SUB(CURDATE(), INTERVAL 14 DAY) <= date(created_date) GROUP BY DATE_FORMAT(created_date, \"%Y-%m-%d\")", nativeQuery = true)
    List<String[]> findStatistics(String type);

    @Query(value = "select DATE_FORMAT(created_date, \"%Y-%m-%d\") AS time, COUNT(uuid) AS total from access where DATE_SUB(CURDATE(), INTERVAL 2 DAY) <= date(created_date) GROUP BY DATE_FORMAT(created_date, \"%Y-%m-%d\")", nativeQuery = true)
    List<String[]> findAllByTwoDays();

    @Query("select a.title as name, COUNT(a.uuid) as y from Access a group by a.title")
    List<Map<String, String>> findScene();

    @Query("select distinct a.type from Access a")
    List<String> findAllType();

    Long countAllByUser(String user);

    @Query(value = "select a.created_date from access a where a.uuid = (select uuid, max(created_date) from access a).uuid DESC LIMIT 1 ", nativeQuery = true)
    Date findLastAccessDateByUser(String user);
}
