package com.jemmy.framework.admin.key;

import com.jemmy.framework.controller.JpaRepository;

public interface KeyRepository extends JpaRepository<Key> {

    Key findByPrivateKey(String privateKey);

}
