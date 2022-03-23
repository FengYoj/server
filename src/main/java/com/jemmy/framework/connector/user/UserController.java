package com.jemmy.framework.connector.user;

import com.jemmy.framework.component.increase.Increase;
import com.jemmy.framework.component.increase.IncreaseController;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.LockMap;
import com.jemmy.framework.utils.SpringBeanUtils;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.result.Result;

public class UserController<U extends User, R extends UserRepository<U>> extends JpaController<U, R> {

    private final IncreaseController increaseController = SpringBeanUtils.getBean(IncreaseController.class);

    private final LockMap<String> keyLock = LockMap.forLargeKeySet();

    private final String prefix;

    public UserController(String prefix) {
        this.prefix = prefix;
    }

    public Result<String> save(U entity) {
        try {
            keyLock.lock(this.getClass().getName() + "-Save");

            if (StringUtils.isBlank(entity.getUid())) {
                this.setUid(entity);
            }

            return super.controller.save(entity);
        } catch (InterruptedException e) {
            return Result.HTTP500().setMessage("线程锁异常").putMessage(e.getMessage()).toObject();
        } finally {
            keyLock.unlock(this.getClass().getName() + "-Save");
        }
    }

    protected void setUid(U user) {
        Result<Increase> increase = increaseController.get(this.getEntity().getSimpleName(), this.prefix, 1000L);

        if (increase.isBlank()) {
            throw new RuntimeException(increase.getMessage());
        }

        user.setUid(increase.getData().getId());
    }

    public Integer getTodayNewUsersNumber() {
        return repository.findTodayNewUsersNumber();
    }

    public Integer getYesterdayNewUsersNumber() {
        return repository.findYesterdayNewUsersNumber();
    }
}
