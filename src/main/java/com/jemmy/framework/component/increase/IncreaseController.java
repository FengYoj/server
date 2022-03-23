package com.jemmy.framework.component.increase;

import com.jemmy.framework.component.increase.record.IncreaseRecordController;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.result.Result;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncreaseController extends JpaController<Increase, IncreaseRepository> {

    private final IncreaseRecordController increaseRecordController;

    public IncreaseController(IncreaseRecordController increaseRecordController) {
        this.increaseRecordController = increaseRecordController;
    }

    /**
     * 获取自增实体
     * @param name 命名
     * @param prefix 前缀
     * @return 自增实体
     */
    public Result<Increase> get(String name, String prefix) {
        return controller.callbackSave(new Increase(name, prefix, increaseRecordController.findByName(name)));
    }

    /**
     * 获取自增实体，自定义起始值
     * @param name 命名
     * @param prefix 前缀
     * @param start 起始值
     * @return 自增实体
     */
    public Result<Increase> get(String name, String prefix, Long start) {
        return controller.callbackSave(new Increase(name, prefix, increaseRecordController.findByName(name, start)));
    }
}
