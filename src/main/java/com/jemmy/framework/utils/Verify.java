package com.jemmy.framework.utils;

import com.jemmy.framework.utils.result.Result;
import lombok.Data;

@Data
public class Verify {

    private Boolean valid;

    private String message;

    private String code;

    public Verify() {
    }

    public Verify(Boolean valid, String message, String code) {
        this.valid = valid;
        this.message = message;
        this.code = code;
    }

    /**
     * 是否有效的
     * @return 判断结果
     */
    public Boolean isValid() {
        return valid;
    }

    /**
     * 是否无效的
     * @return 判断结果
     */
    public Boolean isInvalid() {
        return !valid;
    }

    public static Verify valid() {
        return new Verify(true, "valid", "VALID");
    }

    /**
     * 失效的
     * @param message 信息
     * @param code 错误码
     * @return 实体
     */
    public static Verify invalid(String message, String code) {
        return new Verify(false, message, code);
    }

    public static Verify of(Boolean valid, String message, String code) {
        return new Verify(valid, message, code);
    }

    public <T> Result<T> toStatus() {
        return Result.code(code, message);
    }
}
