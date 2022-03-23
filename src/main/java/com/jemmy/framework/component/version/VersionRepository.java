package com.jemmy.framework.component.version;

import com.jemmy.framework.controller.JpaRepository;

public interface VersionRepository extends JpaRepository<Version> {

    Version findByName(String name);

}
