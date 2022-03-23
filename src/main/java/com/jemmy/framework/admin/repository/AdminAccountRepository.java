package com.jemmy.framework.admin.repository;

import com.jemmy.framework.admin.dao.AdminAccount;
import com.jemmy.framework.controller.JpaRepository;

public interface AdminAccountRepository extends JpaRepository<AdminAccount> {
    AdminAccount findByUsername(String username);

    Boolean existsByUsername(String username);
}
