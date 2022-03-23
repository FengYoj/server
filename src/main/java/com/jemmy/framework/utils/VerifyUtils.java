package com.jemmy.framework.utils;

import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.utils.result.Result;

public class VerifyUtils {

    public static <E> E status(Result<E> result, String message) {
        if (result.isBlank()) {
            // Put exception message
            result.putMessage(message);

            // Throw parameter exception
            throw new ResultException(result);
        }

        // Get data
        return result.getData();
    }

    public static <E> E status(Result<E> result) {
        if (result.isBlank()) {
            // Throw parameter exception
            throw new ResultException(result);
        }

        // Get data
        return result.getData();
    }

    public static <E> E param(E param, String message) {
        if (param instanceof String && StringUtils.isBlank((String) param) || param == null) {
            // Throw parameter exception
            throw new ResultException(Result.HTTP400().putMessage(message));
        }

        return param;
    }

    public static <E> E param(E param) {
        if (param instanceof String && StringUtils.isBlank((String) param) || param == null) {
            // Throw parameter exception
            throw new ResultException(Result.HTTP400());
        }

        return param;
    }
}
