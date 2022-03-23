package com.jemmy.framework.component.access;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.result.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@AutoAPI
public class AccessController extends JpaController<Access, AccessRepository> {

    @AutoAdmin(pages = AutoAdmin.Page.Table)
    public static class _ADMIN extends Admin<Access, AccessController> {}

    @Post
    public Result<String> save(@AutoParam Access access, HttpServletRequest request) {
        // 写入 IP 地址
        access.setIp(IpUtil.getIpAddr(request));
        // 保存
        return super.controller.save(access);
    }

    @Get(path = RequestPath.ADMIN)
    public Result<?> getAllChartData() {
        List<Map<String, Object>> res = new ArrayList<>();

        List<String> types = repository.findAllType();

        for (String type : types) {
            res.add(new HashMap<>() {
                {
                    put("name", AccessTypeName.getName(type));
                    put("data", getStatistics(type));
                }
            });
        }

        return Result.HTTP200().setData(res);
    }

    public List<Long> getTwoDaysAccess() {
        return statistics.getValueByDay(2);
    }

    private List<Long> getStatistics(String type) {
        return statistics.getValueByDay(14, type);
    }

    public Long getCountByUser(User user) {
        return statistics.getValue(user.getUuid());
    }

    public Date getLastAccessDateByUser(User user) {
        return statistics.getModifiedDate(user.getUuid());
    }
}
