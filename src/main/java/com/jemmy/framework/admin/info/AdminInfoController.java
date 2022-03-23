package com.jemmy.framework.admin.info;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.admin.controller.ExceptionController;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.component.access.AccessController;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.order.OrderStatisticsController;
import com.jemmy.framework.component.user.UserController;
import com.jemmy.framework.utils.result.Result;

import java.util.List;

@AutoAPI("Info")
public class AdminInfoController {

    private final AccessController accessController;

    private final UserController userController;

    private final ExceptionController exceptionController;

    private final OrderStatisticsController orderViewController;

    public AdminInfoController(AccessController accessController, UserController userController, ExceptionController exceptionController, OrderStatisticsController orderViewController) {
        this.accessController = accessController;
        this.userController = userController;
        this.exceptionController = exceptionController;
        this.orderViewController = orderViewController;
    }

    @Get(path = RequestPath.ADMIN)
    public Result<JemmyJson> getStatistics() {
        JemmyJson json = new JemmyJson();

        // 访问量
        List<Long> apiVisits = accessController.getTwoDaysAccess();
        // 今日访问量
        Long totalVisitsToday = apiVisits.get(1);
        // 昨日访问量
        Long totalVisitsYesterday = apiVisits.get(0);
        // 较昨日访问量之差
        Long visitsCompare = totalVisitsToday - totalVisitsYesterday;
        json.put("visitsToday", totalVisitsToday);
        json.put("visitsCompare", visitsCompare);

        // 新增用户数量
        Integer usersToday = userController.getTodayNewUsersNumber();
        Integer usersCompare = usersToday - userController.getYesterdayNewUsersNumber();
        json.put("usersToday", usersToday);
        json.put("usersCompare", usersCompare);

        this.processExceptionData(json);
        this.processOrderData(json);

        return Result.<JemmyJson>HTTP200().setData(json);
    }

    /**
     * 处理 异常 数据
     * @param json json
     */
    private void processExceptionData(JemmyJson json) {
        var today = exceptionController.getTodayNumber();
        var yesterday = exceptionController.getYesterdayNumber();

        json.put("exceptionsToday", today);
        json.put("exceptionsCompare", today - yesterday);
    }

    private void processOrderData(JemmyJson json) {
        var today = orderViewController.getTodayAmount();
        var yesterday = orderViewController.getYesterdayAmount();

        json.put("orderToday", today);
        json.put("orderCompare", today - yesterday);
    }
}
