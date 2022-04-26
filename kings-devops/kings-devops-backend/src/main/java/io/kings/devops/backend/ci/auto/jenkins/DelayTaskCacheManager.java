package io.kings.devops.backend.ci.auto.jenkins;

import io.kings.devops.backend.ci.auto.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * <pre>
 *     对延迟执行的任务的处理
 *     1、手工任务会增删
 *     2、定时任务只会删
 * </pre>
 *
 * @author lun.wang
 * @date 2022/3/25 5:20 PM
 * @since v2.5
 */
interface DelayTaskCacheManager {

    DelayedTaskContext cached(String projectKey);

    void remove(String projectKey);

    void caching(String projectKey, DelayedTaskContext context);

    @AllArgsConstructor
    final class DelayedTaskContext {

        //为了记录信息 所以吧上一次的延迟时间存下来
        private final long mills;
        private final Date time = new Date();
        @NonNull
        final ScheduledFuture<?> future;

        String information() {
            return "{time:" + DateTimeFormatter.DEFAULT.format(time) + ",thatMills:" + mills + "}";
        }
    }
}
