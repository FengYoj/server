package com.jemmy.framework.interceptor;

import com.jemmy.framework.admin.allowlist.AllowlistController;
import com.jemmy.framework.admin.blocklist.Blocklist;
import com.jemmy.framework.admin.blocklist.BlocklistController;
import com.jemmy.framework.annotation.HandlerInterceptorConfig;
import com.jemmy.framework.component.access.Access;
import com.jemmy.framework.component.access.AccessController;
import com.jemmy.framework.component.message.Message;
import com.jemmy.framework.component.message.MessageSource;
import com.jemmy.framework.config.SafetyConfig;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.SpringBeanUtils;
import com.jemmy.framework.utils.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HandlerInterceptor implements org.springframework.web.servlet.HandlerInterceptor {

    // 黑名单控制器
    private final BlocklistController blacklistController = SpringBeanUtils.getBean(BlocklistController.class);

    // 准许名单
    private final AllowlistController allowlistController = SpringBeanUtils.getBean(AllowlistController.class);

    // 休眠名单
    private final static List<String> sleepList = new ArrayList<>();

    // 访问频率
    private final static Map<String, Long> frequency = new HashMap<>();

    // 缓存访问信息
    private final static Map<String, AccessInfo> access = new HashMap<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object o) {
        String ip = IpUtil.getIpAddr(request);

        AccessInfo info;

        if (access.containsKey(ip)) {
            info = access.get(ip);
        } else {
            info = new AccessInfo(ip);

            // put to access map
            access.put(ip, info);
        }

        if (info.getBlacklist()) {
            return false;
        }

        // 排除资源拦截器
        if (!(o instanceof ResourceHttpRequestHandler)) {

            // 是否开启访问限制功能
            if (SafetyConfig.block) {

                // 是否处于睡眠名单中
                if (sleepList.contains(ip)) {
                    // 禁止访问
                    return false;
                }

                // 判断访问 IP 内是否包含当前 IP
                if (frequency.containsKey(ip)) {
                    Long num = frequency.get(ip);

                    // 超过限制且非准许名单
                    if (num >= SafetyConfig.frequency && !info.getAllowlist()) {

                        switch (SafetyConfig.execute) {
                            case "block":
                                Blocklist blocklist = new Blocklist();

                                blocklist.setIp(ip);
                                blocklist.setCreator("System");
                                blocklist.setNote("超出访问频率限制，系统自动添加为限制名单。");

                                blacklistController.controller.save(blocklist);

                                // info map set blacklist
                                info.setBlacklist(true);

                                // 禁止访问
                                return false;
                            case "sleep":
                                sleepList.add(ip);

                                // 禁止访问
                                return false;
                            case "forward":
                                Message.info(MessageSource.REQUEST_INTERCEPTION, String.format("IP地址为: %s 的请求次数超过访问限制。", ip));

                                // 移除，避免多次触发推送消息
                                frequency.remove(ip);
                        }
                    }

                    frequency.put(ip, ++num);
                } else {
                    frequency.put(ip, 1L);
                }
            }

            new Thread(new AccessRecord(request, ip, o)).start();
        }

        return true;
    }

    // 实现Runnable接口，作为线程的实现类
    private static class AccessRecord implements Runnable {
        private static AccessController accessController = null;
        private final HttpServletRequest request;
        private final String ip;
        private final Object o;

        public AccessRecord(HttpServletRequest request, String ip, Object o) {
            this.request = request;
            this.ip = ip;
            this.o = o;
        }

        public void run() {
            Access access = new Access();

            access.setIp(ip);
            access.setType("api");
            access.setAgent(request.getHeader("User-Agent"));
            access.setMethod(request.getMethod());
            access.setClassName(o.toString());
            access.setUser(request.getHeader("User-ID"));

            // 当文档路径为空时写入服务路径
            access.setPath(StringUtils.defaults(request.getContextPath(), request.getServletPath()));

            HandlerInterceptorConfig config = ((HandlerMethod) o).getMethod().getDeclaringClass().getAnnotation(HandlerInterceptorConfig.class);

            if (config != null) {
                access.setTitle(config.title());
            }

            if (accessController == null) {
                accessController = SpringBeanUtils.getBean(AccessController.class);
            }

            accessController.controller.onlySave(access);
        }
    }

    /**
     * 每隔 60 秒清除一次 frequency 数据
     */
    @Scheduled(fixedRate = 60000)
    private void clearFrequency() {
        // 清空 frequency 数据
        HandlerInterceptor.frequency.clear();
    }

    /**
     * 每隔 5 分钟清除一次 sleepList 数据
     */
    @Scheduled(fixedRate = 300000)
    private void clearSleepList() {
        // 清空 sleepList 数据
        HandlerInterceptor.sleepList.clear();
    }

    /**
     * 每隔 60 分钟清除一次 access 数据
     */
    @Scheduled(fixedRate = 3600000)
    private void clearAccess() {
        // 清空 access 数据
        HandlerInterceptor.access.clear();
    }

    private class AccessInfo {

        private final String ip;

        private Boolean blacklist;

        private Boolean allowlist;

        public AccessInfo(String ip) {
            this.ip = ip;
        }

        public Boolean getBlacklist() {

            if (blacklist == null) {
                blacklist = blacklistController.existsByIp(ip);
            }

            return blacklist;
        }

        public void setBlacklist(Boolean blacklist) {
            this.blacklist = blacklist;
        }

        public Boolean getAllowlist() {

            if (allowlist == null) {
                allowlist = allowlistController.existsByIp(ip);
            }

            return allowlist;
        }

        public void setAllowlist(Boolean allowlist) {
            this.allowlist = allowlist;
        }
    }
}
