package com.jemmy.framework.component.statistics;

import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.LockMap;
import com.jemmy.framework.utils.SpringBeanUtils;
import com.jemmy.framework.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatisticsContinuation<T extends EntityKey> {

    private static final LockMap<Class<?>> keyLock = LockMap.forLargeKeySet();

    private final Boolean use;

    private final StatisticsController controller;

    private final Class<T> entity;

    private final List<Field> fields = new ArrayList<>();

    private final List<String> fieldNames = new ArrayList<>();

    private final String name;

    private final List<StatisticsCrucial> crucialList = new ArrayList<>();

    public StatisticsContinuation(Class<T> entity) {
        this.controller = SpringBeanUtils.getBean(StatisticsController.class);
        this.entity = entity;
        this.name = entity.getSimpleName();

        // 判断是否开启统计
        use = entity.isAnnotationPresent(StatisticsEntity.class);

        // 获取统计字段
        if (use) {
            StatisticsEntity statisticsEntity = entity.getDeclaredAnnotation(StatisticsEntity.class);

            try {
                var cs = statisticsEntity.crucial();

                if (cs.length > 0) {
                    for (Class<? extends StatisticsCrucial> c: cs) {
                        crucialList.add(c.getConstructor().newInstance());
                    }
                }

                this.getField();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Long getValue() {
        return getValue(controller.find(name));
    }

    public Long getValue(String crucial) {
        return getValue(controller.find(name, crucial));
    }

    public Long getValueByDayBefore(Integer day) {
        return getValue(controller.findByDayBefore(name, day));
    }

    public Long getValueByDayBefore(Integer day, String crucial) {
        return getValue(controller.findByDayBefore(name, day, crucial));
    }

    public Date getModifiedDate(String crucial) {
        Statistics statistics = controller.find(name, crucial);

        if (statistics == null) {
            return null;
        }

        return statistics.getModifiedDate();
    }

    public Long getValueByField(String field) {
        return getValue(controller.findByField(name, field));
    }

    public Long getValueByField(String field, String crucial) {
        return getValue(controller.findByField(name, field, crucial));
    }

    public List<Long> getValueByDay(Integer day) {
        return controller.getValueByDay(name, day);
    }

    public List<Long> getValueByDay(Integer day, String crucial) {
        return controller.getValueByDay(name, day, crucial);
    }

    public void add(T e) {
        // 未使用统计功能
        if (!use) {
            return;
        }

        new Thread(new AddStatistics(fields.size() > 0, e)).start();
    }

    private class AddStatistics implements Runnable {

        private final Boolean field;

        private final T entity;

        public AddStatistics(Boolean field, T entity) {
            this.field = field;
            this.entity = entity;
        }

        public void run() {
            try {
                keyLock.lock(getClass());

                if (field) {
                    statistics(entity);
                } else {
                    noFieldStatistics(entity);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                keyLock.unlock(getClass());
            }
        }
    }

    private void statistics(T e) {
        try {
            for (StatisticsCrucial crucial : crucialList) {
                String key = getKey(e, crucial);

                switch (crucial.type()) {
                    case TOTAL:
                        this.processTotal(key, e);
                        break;
                    case DETAILED:
                        this.processDetailed(key);
                        break;
                    default:
                        this.processTotal(key, e);
                        this.processDetailed(key);
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void noFieldStatistics(T e) {
        try {
            for (StatisticsCrucial crucial : crucialList) {
                String key = getKey(e, crucial);

                switch (crucial.type()) {
                    case TOTAL:
                        this.processTotal(key);
                        break;
                    case DETAILED:
                        this.processDetailed(key);
                        break;
                    default:
                        this.processTotal(key);
                        this.processDetailed(key);
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void processDetailed(String key) {
        Statistics statistics;

        if (StringUtils.isExist(key)) {
            statistics = controller.findByToday(name, key);
        } else {
            statistics = controller.findByToday(name);
        }

        if (statistics == null) {
            statistics = this.getStatistics(key, StatisticsType.DETAILED);
        }

        statistics.setValue(statistics.getValue() + 1);

        controller.save(statistics);
    }

    private void processTotal(String key) {
        Statistics statistics;

        if (StringUtils.isExist(key)) {
            statistics = controller.find(name, key);
        } else {
            statistics = controller.find(name);
        }

        if (statistics == null) {
            statistics = this.getStatistics(key, StatisticsType.TOTAL);
        }

        statistics.setValue(statistics.getValue() + 1);

        controller.save(statistics);
    }

    private void processTotal(String key, T e) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, Statistics> statisticsMap;
        List<Statistics> statisticsList = new ArrayList<>();

        if (StringUtils.isExist(key)) {
            statisticsMap = controller.findAllToMap(name, fieldNames, key);
        } else {
            statisticsMap = controller.findAllToMap(name, fieldNames);
        }

        for (Field field : fields) {
            StatisticsAttr attr = field.getDeclaredAnnotation(StatisticsAttr.class);

            // 是否存在判断条件
            if (!attr.condition().equals(StatisticsCondition.class)) {
                StatisticsCondition condition = attr.condition().getConstructor().newInstance();

                String name = condition.name();
                Object value = condition.value();

                // 条件不符合要求，不进行统计
                if (!entity.getField(name).get(e).equals(value)) {
                    continue;
                }
            }

            Statistics statistics = statisticsMap.get(field.getName());

            if (statistics == null) {
                statistics = this.getStatistics(field.getName(), key);
            }

            statistics.setValue(statistics.getValue() + this.getValue(field.get(e)));

            statisticsList.add(statistics);
        }

        controller.saveAll(statisticsList);
    }

    private Long getValue(Object v) {
        if (v == null) {
            return 0L;
        }

        if (v instanceof Long) {
            return (Long) v;
        }

        if (v instanceof Integer) {
            return ((Integer) v).longValue();
        }

        if (v instanceof Double) {
            return ((Double) v).longValue();
        }

        return 0L;
    }

    private Statistics getStatistics(String field, String key) {
        if (StringUtils.isExist(key)) {
            return Statistics.ofField(name, field, key);
        } else {
            return Statistics.ofField(name, field);
        }
    }

    private Statistics getStatistics(String key, StatisticsType type) {
        if (StringUtils.isExist(key)) {
            return Statistics.of(name, key, type);
        } else {
            return Statistics.of(name, type);
        }
    }

    private void getField() {
        for (Field field : EntityUtils.getFields(this.entity)) {
            if (field.isAnnotationPresent(StatisticsAttr.class)) {
                fields.add(field);
                fieldNames.add(field.getName());
            }
        }
    }

    private String getKey(T e, StatisticsCrucial crucial) throws Exception {
        if (crucial == null || crucial.isInitial()) {
            return null;
        }

        Object obj = crucial.getValue(e);

        if (crucial.isEntity()) {
            String id = ((EntityKey) obj).getUuid();

            if (StringUtils.isBlank(id)) {
                throw new Exception("ID cannot be empty");
            }

            return id;
        }

        if (obj == null) {
            return null;
        }

        return obj instanceof String ? (String) obj : JemmyJson.toJSONString(obj);
    }

    private Long getValue(Statistics s) {
        if (s == null) {
            return 0L;
        }

        return s.getValue();
    }
}
