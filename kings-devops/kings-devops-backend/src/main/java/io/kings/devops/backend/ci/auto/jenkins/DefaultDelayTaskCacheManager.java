package io.kings.devops.backend.ci.auto.jenkins;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * 延迟任务缓存实现
 *
 * @author lun.wang
 * @date 2022/3/25 5:35 PM
 * @since v2.5
 */
@Component
class DefaultDelayTaskCacheManager implements DelayTaskCacheManager {

    //延迟任务记录
    private final Map<String, DelayedTaskContext> cache = new ConcurrentHashMap<>(64);

    @Override
    public DelayedTaskContext cached(String projectKey) {
        return cache.get(projectKey);
    }

    @Override
    public void remove(String projectKey) {
        cache.remove(projectKey);
    }

    @Override
    public void caching(String projectKey, DelayedTaskContext context) {
        cache.put(projectKey, context);
    }
}
