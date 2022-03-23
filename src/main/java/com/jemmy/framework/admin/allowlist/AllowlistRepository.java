package com.jemmy.framework.admin.allowlist;

import com.jemmy.framework.controller.JpaRepository;

public interface AllowlistRepository extends JpaRepository<Allowlist> {

    Boolean existsByIp(String ip);

}
