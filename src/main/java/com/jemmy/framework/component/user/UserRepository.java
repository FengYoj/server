package com.jemmy.framework.component.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface UserRepository extends com.jemmy.framework.connector.user.UserRepository<User> {
    User findByPhone(String phone);

    User findByWxOpenid(String openid);

    User findByTtOpenid(String openid);

    @Query("select u from User u where u.login = true")
    Page<User> findAllByLogin(Pageable pageable);

    User findByUuidAndToken(String uuid, String token);

    @Transactional
    @Modifying
    @Query("update User u set u.token = ?2 where u.uuid = ?1")
    void updateToken(String uuid, String token);

    @Query("select u.uuid from User u where u.mpOpenid = ?1")
    String findUuidByMpOpenid(String mpOpenid);

    User findByMpOpenid(String mpOpenid);
}
