package com.jemmy.framework.component.validate_code;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.data.redis.RedisHash;
import com.jemmy.framework.utils.Verify;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Set;

@AutoAPI("ValidateCode")
@RestController
public class ValidateCodeController {
    private final RedisHash<ValidateCode> redisHash;

    public ValidateCodeController() {
        redisHash = new RedisHash<>("validateCode");
    }

    @Get
    public Result<ValidateCode> getCode() {
        return Result.<ValidateCode>of(ResultCode.HTTP200).setData(this.getImage());
    }

    public ValidateCode get() {
        // 默认时效为 10 分钟
        return this.get(10 * 60 * 1000L);
    }

    public ValidateCode get(Long aging) {
        ValidateCode code = ValidateCodeUtil.getCode(aging);
        // 写入 redis 缓存
        redisHash.put(code.getId(), code);
        // 返回实体
        return code;
    }

    public ValidateCode get(Long aging, String check) {
        ValidateCode code = ValidateCodeUtil.getCode(aging);
        code.setCheck(check);
        // 写入 redis 缓存
        redisHash.put(code.getId(), code);
        // 返回实体
        return code;
    }

    public ValidateCode getImage() {
        // 默认时效为 10 分钟
        return this.getImage(10 * 60 * 1000L);
    }

    public ValidateCode getImage(Long aging) {
        ValidateCode code = ValidateCodeUtil.getImageCode(aging);
        // 写入 redis 缓存
        redisHash.put(code.getId(), code);
        // 返回实体
        return code;
    }

    public Verify verify(String id, String code) {
        // 判断 ID 是否存在
        if (!redisHash.exists(id)) {
            return Verify.invalid("Data does not exist", "DATA_NOT_EXIST");
        }

        ValidateCode validateCode = redisHash.get(id);

        if (!validateCode.getCode().equals(code)) {
            return Verify.invalid("Verification code error", "CODE_ERROR");
        }

        if (validateCode.getCreateDate().getTime() + validateCode.getAging() < new Date().getTime()) {
            return Verify.invalid("Verification code has expired", "CODE_EXPIRED");
        }

        return Verify.valid();
    }

    public Verify verify(String id, String code, String check) {
        // 判断 ID 是否存在
        if (!redisHash.exists(id)) {
            return Verify.invalid("Data does not exist", "DATA_NOT_EXIST");
        }

        ValidateCode validateCode = redisHash.get(id);

        // 验证码错误
        if (!validateCode.getCode().equals(code)) {
            return Verify.invalid("Verification code error", "CODE_ERROR");
        }

        // 超时
        if (validateCode.getCreateDate().getTime() + validateCode.getAging() < new Date().getTime()) {
            // 过期删除验证码实体
            redisHash.delete(id);
            // 返回超时
            return Verify.invalid("Verification code has expired", "CODE_EXPIRED");
        }

        // 校验失败
        if (!validateCode.getCheck().equals(check)) {
            return Verify.invalid("check failed", "CHECK_FAILED");
        }

        return Verify.valid();
    }

    /**
     * 定时清理任务：每天凌晨 3 点
     */
    @Scheduled(cron = "0 0 3 * * ?")
    private void regularCleanup() {
        Set<String> set = redisHash.keys();

        for (String s : set) {
            ValidateCode code = redisHash.get(s);

            // 判断超时
            if (code.getCreateDate().getTime() + code.getAging() < new Date().getTime()) {
                // 过期删除验证码实体
                redisHash.delete(s);
            }
        }
    }
}
