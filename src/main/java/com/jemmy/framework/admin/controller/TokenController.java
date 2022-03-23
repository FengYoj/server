package com.jemmy.framework.admin.controller;

import com.jemmy.framework.admin.dao.Token;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.data.redis.RedisHash;
import com.jemmy.framework.utils.DateUtils;
import com.jemmy.framework.utils.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
public class TokenController {
    // 时效 2 小时
    public static Long GENERAL_PERIOD = 1000 * 60 * 60 * 2L;
    // 时效 1 天
    public static Long DAY_PERIOD = 1000 * 60 * 60 * 24L;
    // 时效 7 天
    public static Long SEVEN_DAY_PERIOD = DAY_PERIOD * 7;

    private final RedisHash<Token> redisHash;

    public TokenController(RedisConnectionFactory redisConnectionFactory) {
        redisHash = new RedisHash<>("token", redisConnectionFactory);

        scheduledClear();
    }

    /**
     * 获取 token
     *
     * @param nature 性质
     * @return token
     */

    public String getTokenStr(String nature, Long period) {
        return createToken(nature, null, period).getToken();
    }

    public String getTokenStr(String nature) {
        return createToken(nature, null, GENERAL_PERIOD).getToken();
    }

    public String getTokenStr(String nature, String check) {
        return createToken(nature, check, GENERAL_PERIOD).getToken();
    }

    public String getTokenStr(String nature, String check, Long period) {
        return createToken(nature, check, period).getToken();
    }

    private Token createToken(String nature, String check, Long period) {
        Token token = new Token();
        token.setToken();
        token.setNature(nature);
        token.setTimestamp(new Date().getTime());
        token.setCheck(check);

        if (period != null) {
            token.setPeriod(period);
        }

        redisHash.put(token.getToken(), token);

        return token;
    }

//    /**
//     * 获取 token 状态
//     *
//     * @param isDestroy 是否摧毁，仅在验证通过时摧毁
//     * @return 状态
//     */
//    public Boolean getStatus(Token token, Boolean isDestroy) {
//
//        if (token == null) {
//            return false;
//        }
//
//        if (isDestroy) {
//            tokenRepository.delete(token);
//        }
//
//        return true;
//    }

    public Boolean getStatus(String token_str, String nature) {
        return getToken(token_str, nature) != null;
    }

    public Boolean getStatus(String token_str, String nature, String check) {
        return getToken(token_str, nature, check) != null;
    }

    private Token getToken(String token_str, String nature, String check) {
        if (StringUtils.isBlank(token_str) || StringUtils.isBlank(check)) {
            return null;
        }

        Token token = redisHash.get(token_str);

        if (isNormal(token) && token.getNature().equals(nature) && token.getCheck().equals(check)) {
            return token;
        }

        return null;
    }

    private Token getToken(String token_str, String nature) {
        if (StringUtils.isBlank(token_str)) {
            return null;
        }

        Token token = redisHash.get(token_str);

        if (isNormal(token) && token.getNature().equals(nature)) {
            return token;
        }

        return null;
    }

    private Boolean isNormal(Token token) {
        return token != null && token.getUpdateDate() != null &&
                !DateUtils.isDelayExpired(token.getUpdateDate(), token.getPeriod());
    }

    /**
     * 更新 token
     *
     * @param token token
     */
    public void updateToken(String token) {
        Token entity = redisHash.get(token);
        entity.setUpdateDate(new Date());
        redisHash.put(token, entity);
    }

    /**
     * 添加定时任务，每天凌晨 3 点触发
     */
    @Scheduled(cron = "0 0 3 * * ?")
    private void scheduledClear() {
        Set<String> set = redisHash.keys();
        List<String> keys = new ArrayList<>();

        for (String key : set) {
            Token token = redisHash.get(key);

            if (token.getUpdateDate() == null || DateUtils.isDelayExpired(token.getUpdateDate(), token.getPeriod())) {
                keys.add(key);
            }
        }

        if (keys.size() > 0) {
            redisHash.delete(keys.toArray(new String[]{}));
        }
    }
}