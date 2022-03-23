package com.jemmy.framework.connector.user;

import com.jemmy.framework.controller.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserRepository<U extends User> extends JpaRepository<U> {
    @Query(value = "select COUNT(uuid) AS total from user where TO_DAYS(created_date) = TO_DAYS(NOW())", nativeQuery = true)
    Integer findTodayNewUsersNumber();

    @Query(value = "select COUNT(uuid) AS total from user where TO_DAYS(NOW()) - TO_DAYS(created_date) <= 1", nativeQuery = true)
    Integer findYesterdayNewUsersNumber();
}
