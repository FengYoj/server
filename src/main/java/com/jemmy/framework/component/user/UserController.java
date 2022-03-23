package com.jemmy.framework.component.user;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.admin.AutoMenu;
import com.jemmy.framework.auto.api.Authority;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Delete;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.auto.param.AutoParamType;
import com.jemmy.framework.auto.param.ParamMethod;
import com.jemmy.framework.component.access.Access;
import com.jemmy.framework.component.access.AccessController;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.order.OrderStatisticsController;
import com.jemmy.framework.component.resources.image.ImageController;
import com.jemmy.framework.component.resources.image.ResourceImage;
import com.jemmy.framework.utils.ListPage;
import com.jemmy.framework.utils.LockMap;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.UuidUtils;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@AutoAPI
public class UserController extends com.jemmy.framework.connector.user.UserController<User, UserRepository> {

    private final LockMap<String> keyLock = LockMap.forLargeKeySet();

    private final ImageController imageController;

    private final AccessController accessController;

    private final OrderStatisticsController orderStatisticsController;

    @AutoAdmin
    @AutoMenu
    public static class _ADMIN extends Admin<User, UserController> {}

    public UserController(ImageController imageController, AccessController accessController, OrderStatisticsController orderStatisticsController) {
        super("GU");
        this.imageController = imageController;
        this.accessController = accessController;
        this.orderStatisticsController = orderStatisticsController;
    }

    @Post(path = RequestPath.ADMIN)
    public Result<?> findAllToPage(@AutoParam Integer page, @AutoParam(required = false, defaults = "10") Integer size, @AutoParam(required = false) String search) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        List<JemmyJson> res = new ArrayList<>();

        Page<User> userPage;

        // 是否存在搜索条件
        if (StringUtils.isExist(search)) {
            Result<Page<User>> result = controller.searchToPage(page, size, search);

            if (result.isBlank()) {
                return result;
            }

            userPage = result.getData();
        } else {
            userPage = repository.findAllByLogin(PageRequest.of(page, size, Sort.Direction.DESC, "createdDate"));
        }

        for (User user : userPage) {
            JemmyJson json = new JemmyJson(user);

            json.put("accessFrequency", accessController.getCountByUser(user));
            json.put("orderTotal", orderStatisticsController.getTotalAmountByUser(user));
            json.put("lastAccess", accessController.getLastAccessDateByUser(user));

            res.add(json);
        }

        return Result.HTTP200().setData(new ListPage<>(res, PageRequest.of(page, size, Sort.Direction.DESC, "createdDate"), userPage.getTotalElements()));
    }

    @Post(path = RequestPath.ADMIN)
    public Result<?> changeDisable(@AutoParam User user, @AutoParam Boolean disable) {
        if (disable && !user.getStatus().equals(1)) {
            return Result.code("NOT_ALLOWED");
        }

        if (disable && user.getStatus().equals(3)) {
            return Result.HTTP200();
        }

        user.setStatus(disable ? 3 : 1);

        return save(user).toEmpty();
    }

    @Get
    public Result<User> findByUuid(@AutoParam String i, @AutoParam(method = ParamMethod.HEADER, value = "User-Token") String token) {
        User user = repository.findByUuidAndToken(i, token);

        if (user == null) {
            return Result.HTTP403();
        }

        return Result.HTTP200(user);
    }

    public String updateToken(User user) {
        // 获取 32 位随机 UUID
        String token = UuidUtils.getUUID32();

        // 更新 token 字段
        repository.updateToken(user.getUuid(), token);

        return token;
    }

    @Get
    public Result<List<User>> search(@AutoParam String search) {
        return controller.search(search);
    }

    @Get
    public Result<User> loginOrCreate(@AutoParam(required = false) String wxOpenid, @AutoParam(required = false) String ttOpenid, HttpServletRequest request) {

        try {
            keyLock.lock(this.getClass().getName() + "-LoginOrCreate");

            if (StringUtils.isExist(wxOpenid)) {
                User user = repository.findByWxOpenid(wxOpenid);

                if (user != null) {
                    return Result.<User>of(ResultCode.HTTP200).setData(user);
                }
            }

            if (StringUtils.isExist(ttOpenid)) {
                User user = repository.findByTtOpenid(ttOpenid);

                if (user != null) {
                    return Result.<User>of(ResultCode.HTTP200).setData(user);
                }
            }

            User user = new User();

            user.setIp(IpUtil.getIpAddr(request));
            // Set uid
            this.setUid(user);

            return controller.callbackSave(user);
        } catch (InterruptedException e) {
            return new Result<>(ResultCode.HTTP500).setMessage("线程锁异常").putMessage(e.getMessage()).toObject();
        } finally {
            keyLock.unlock(this.getClass().getName() + "-LoginOrCreate");
        }
    }

    @Post
    public Result<User> updateAnonymous(@AutoParam(type = AutoParamType.JSON) JemmyJson param, @AutoParam User user) throws IOException {
        Result<ResourceImage> result = imageController.upload(param.getString("avatarUrl"));

        if (result.isBlank()) {
            return result.toObject();
        }

        user.setProvider(param.getString("provider"));
        user.setAvatar(result.getData());
        user.setUsername(param.getString("nickName"));
        user.setCountry(param.getString("country"));
        user.setProvince(param.getString("province"));
        user.setCity(param.getString("city"));
        user.setWxOpenid(param.getString("wxOpenid"));
        user.setLogin(true);

        return controller.callbackSave(user);
    }

    @Post
    public Result<User> bindPhoneNumber(@AutoParam String phone, @AutoParam String code, @AutoParam User user) {
        if (StringUtils.isExist(user.getPhone())) {
            return Result.<User>of(ResultCode.HTTP409).setMessage("The current account is bound to a number").setCode("BOUND");
        }

        User u = repository.findByPhone(phone);

        if (u != null) {
            return Result.<User>of(ResultCode.HTTP409)
                    .setMessage("The current number bound account is " + u.getUsername())
                    .setCode("BE_BOUND")
                    .setInfo(u.getUsername());
        }

        user.setPhone(phone);
        user.setCode(code);

        return controller.callbackSave(user);
    }

    @Post
    public Result<User> loginByPhoneNumber(@AutoParam String phone, @AutoParam String user_id) {
        User u = repository.findByPhone(phone);

        if (u == null) {
            return Result.of(ResultCode.HTTP404);
        }

        // 删除当前匿名用户
        controller.deleteByUuid(user_id);

        return Result.<User>of(ResultCode.HTTP200).setData(u);
    }

    @Delete(path = RequestPath.ADMIN)
    public Result<?> delete(@AutoParam User user) {
        return controller.delete(user);
    }

    @Get(authority = Authority.ROOT)
    public Result<?> updateAvatar() {
        List<User> users = repository.findAll();

        for (User user : users) {
            if (user.getAvatar() == null && user.getAvatarUrl() != null) {
                try {
                    var status = imageController.upload(user.getAvatarUrl());
                    user.setAvatar(status.getData());
                    super.save(user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return Result.HTTP200();
    }

    public Result<String> findUuidByMpOpenid(String mpOpenid) {
        return Result.data(repository.findUuidByMpOpenid(mpOpenid));
    }

    public Result<User> findByMpOpenid(String mpOpenid) {
        return Result.data(repository.findByMpOpenid(mpOpenid));
    }
}
