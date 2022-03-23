package com.jemmy.framework.component.increase.record;

import com.jemmy.framework.controller.JpaRepository;

public interface IncreaseRecordRepository extends JpaRepository<IncreaseRecord> {
    IncreaseRecord findByName(String name);
}
