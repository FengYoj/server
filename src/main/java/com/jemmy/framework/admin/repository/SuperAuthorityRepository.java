package com.jemmy.framework.admin.repository;

import com.jemmy.framework.admin.dao.SuperAuthority;
import com.jemmy.framework.controller.JpaRepository;

public interface SuperAuthorityRepository extends JpaRepository<SuperAuthority> {
    boolean existsByKeyAndIp(String key, String ip);
}
