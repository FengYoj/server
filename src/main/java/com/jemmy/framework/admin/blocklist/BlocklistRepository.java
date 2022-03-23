package com.jemmy.framework.admin.blocklist;

import com.jemmy.framework.controller.JpaRepository;

public interface BlocklistRepository extends JpaRepository<Blocklist> {
    Boolean existsByIp(String ip);
}
