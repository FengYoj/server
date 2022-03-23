package com.jemmy.framework.protocol;

import com.jemmy.framework.controller.JpaRepository;

public interface ProtocolRepository extends JpaRepository<Protocol> {

    Protocol findByIdentifier(String identifier);

}
