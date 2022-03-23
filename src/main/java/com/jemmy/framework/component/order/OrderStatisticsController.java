package com.jemmy.framework.component.order;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.user.User;
import com.jemmy.framework.utils.result.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoAPI("OrderChart")
public class OrderStatisticsController {

    private static final Map<String, OrderController<? extends Order, ? extends OrderRepository<?>>> orderControllerMap = new HashMap<>();

    public static void add(String name, OrderController<? extends Order, ? extends OrderRepository<?>> controller) {
        orderControllerMap.put(name, controller);
    }

    /**
     * 获取指定订单表格数据
     * @return 表格数据
     */
    @Get(path = RequestPath.ADMIN)
    public Result<List<Double>> getChartData(@AutoParam String name) {

        if (!orderControllerMap.containsKey(name)) {
            return Result.HTTP404();
        }

        var controller = orderControllerMap.get(name);

        return Result.<List<Double>>HTTP200().setData(controller.getChartData());
    }

    /**
     * 获取所有订单表格数据
     * @return 表格数据
     */
    @Get(path = RequestPath.ADMIN)
    public Result<List<Map<String, Object>>> getAllChartData() {
        List<Map<String, Object>> res = new ArrayList<>();

        for (String key : orderControllerMap.keySet()) {
            var value = orderControllerMap.get(key);

            res.add(new HashMap<>() {
                {
                    put("name", key);
                    put("data", value.getChartData());
                }
            });
        }

        return Result.<List<Map<String, Object>>>HTTP200().setData(res);
    }

    public Double getTodayAmount() {
        double res = 0;

        for (String key : orderControllerMap.keySet()) {
            var value = orderControllerMap.get(key);

            if (value != null) {
                var a = value.getTodayAmount();

                if (a != null) {
                    res += a;
                }
            }
        }

        return res;
    }

    public Double getYesterdayAmount() {
        double res = 0;

        for (String key : orderControllerMap.keySet()) {
            var value = orderControllerMap.get(key);

            if (value != null) {
                var a = value.getYesterdayAmount();

                if (a != null) {
                    res += a;
                }
            }
        }

        return res;
    }

    public Long getTotalAmountByUser(User user) {
        long res = 0L;

        for (String key : orderControllerMap.keySet()) {
            var value = orderControllerMap.get(key);

            if (value != null) {
                var a = value.getTotalAmountByUser(user);

                if (a != null) {
                    res += a;
                }
            }
        }

        return res;
    }
}
