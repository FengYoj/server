package com.jemmy.framework.component.statistics;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.utils.LockMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoAPI
public class StatisticsController {

    private final LockMap<String> keyLock = LockMap.forLargeKeySet();

    private final StatisticsRepository jpaRepository;

    public StatisticsController(StatisticsRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Statistics findByDayBefore(String name, Integer day) {
        return jpaRepository.findByDayBefore(name, day);
    }

    public Statistics findByDayBefore(String name, Integer day, String crucial) {
        return jpaRepository.findByDayBefore(name, day, crucial);
    }

    public List<Long> getValueByDay(String name, Integer day) {
        var list = jpaRepository.findStatistics(name, day);
        // 处理数据
        return StatisticsUtils.daysToLongs(list, day);
    }

    public List<Long> getValueByDay(String name, Integer day, String crucial) {
        var list = jpaRepository.findStatistics(name, day, crucial);
        // 处理数据
        return StatisticsUtils.daysToLongs(list, day);
    }

    public void saveAll(List<Statistics> es) {
        try {
            keyLock.lock(toString());
            // Save
            jpaRepository.saveAll(es);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            keyLock.unlock(toString());
        }
    }

    public void save(Statistics s) {
        try {
            keyLock.lock(toString());
            // Save
            jpaRepository.save(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            keyLock.unlock(toString());
        }
    }

    public Boolean apply(Class<? extends EntityKey> entity) {
        return entity.isAnnotationPresent(StatisticsEntity.class);
    }

    public Statistics findByField(String name, String field) {
        return jpaRepository.findByNameAndField(name, field);
    }

    public Statistics findByField(String name, String field, String crucial) {
        return jpaRepository.findByNameAndFieldAndCrucial(name, field, crucial);
    }

    public Statistics find(String name) {
        return jpaRepository.findByName(name);
    }

    public Statistics find(String name, String crucial) {
        return jpaRepository.findByNameAndCrucial(name, crucial);
    }

    public Statistics findByToday(String name) {
        return jpaRepository.findByNameAndToday(name);
    }

    public Statistics findByToday(String name, String crucial) {
        return jpaRepository.findByNameAndCrucialAndToday(name, crucial);
    }

    public List<Statistics> findAll(String name, List<String> field) {
        return jpaRepository.findByNameAndFieldIn(name, field);
    }

    public List<Statistics> findAll(String name, List<String> field, String crucial) {
        return jpaRepository.findByNameAndFieldInAndCrucial(name, field, crucial);
    }

    public Map<String, Statistics> findAllToMap(String name, List<String> field) {
        return toMap(findAll(name, field));
    }

    public Map<String, Statistics> findAllToMap(String name, List<String> field, String crucial) {
        return toMap(jpaRepository.findByNameAndFieldInAndCrucial(name, field, crucial));
    }

    private Map<String, Statistics> toMap(List<Statistics> list) {
        Map<String, Statistics> res = new HashMap<>();

        if (list.size() <= 0) {
            return res;
        }

        for (Statistics s : list) {
            res.put(s.getField(), s);
        }

        return res;
    }
}
