package com.jemmy.framework.data.redis;

import com.jemmy.framework.utils.SpringBeanUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;

import java.util.Map;
import java.util.Set;

public class RedisHash<T> {
    private final String table;

    private final RedisTemplate<T> redisTemplate;

    private final BoundHashOperations<String, String, T> hash;

    public RedisHash(String table) {
        this.table = table;
        this.redisTemplate = new RedisTemplate<>(SpringBeanUtils.getBean(RedisConnectionFactory.class));
        this.hash = redisTemplate.boundHashOps(table);
    }

    public RedisHash(String table, RedisConnectionFactory redisConnectionFactory) {
        this.table = table;
        this.redisTemplate = new RedisTemplate<>(redisConnectionFactory);
        this.hash = redisTemplate.boundHashOps(table);
    }

    public T get(String key) {
        return hash.get(key);
    }

    public void put(String key, T value) {
        hash.put(key, value);
    }

    public Boolean exists(String key) {
        return hash.hasKey(key);
    }

    public Set<String> keys() {
        return hash.keys();
    }

    /**
     * 删除key
     *
     * @param keys keys
     */
    public void delete(String... keys) {
        hash.delete(keys);
    }

    public Map<String, T> getMap() {
        return hash.entries();
    }
}
