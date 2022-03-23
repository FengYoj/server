package com.jemmy.framework.component.increase.record;

import com.jemmy.framework.controller.JpaController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncreaseRecordController extends JpaController<IncreaseRecord, IncreaseRecordRepository> {

    public Long findByName(String name, Long start) {
        IncreaseRecord record = repository.findByName(name);

        if (record == null) {
            // 保存
            controller.save(new IncreaseRecord(name, start));

            return start;
        }

        // 自增
        record.increase();

        // 保存
        controller.save(record);

        return record.getNumber();
    }

    public Long findByName(String name) {
        return this.findByName(name, 1L);
    }
}
