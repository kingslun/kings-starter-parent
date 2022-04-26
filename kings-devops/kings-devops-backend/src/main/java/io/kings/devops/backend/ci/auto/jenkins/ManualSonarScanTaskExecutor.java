package io.kings.devops.backend.ci.auto.jenkins;

import io.kings.devops.backend.ci.auto.ThreadUtils;
import io.kings.devops.backend.ci.auto.jenkins.DelayTaskCacheManager.DelayedTaskContext;
import io.kings.devops.backend.ci.auto.jenkins.JenkinsTaskTrigger.TriggerContext;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.ToLongFunction;
import lombok.extern.slf4j.Slf4j;

/**
 * 手工任务执行器
 *
 * @author lun.wang
 * @date 2022/3/25 2:38 PM
 * @since v2.5
 */
@Slf4j
class ManualSonarScanTaskExecutor implements SonarScanTaskExecutor {

    private final ToLongFunction<Date> calculateTime = time -> time.getTime()
        - System.currentTimeMillis();
    private final ScheduledExecutorService schedulePool = ThreadUtils.schedulePool(
        Runtime.getRuntime().availableProcessors() / 4, "JenkinsDelayTasker");
    private final JenkinsTaskTrigger jenkinsTaskTrigger;
    private final DelayTaskCacheManager delayTaskCacheManager;

    ManualSonarScanTaskExecutor(JenkinsTaskTrigger jenkinsTaskTrigger,
        DelayTaskCacheManager delayTaskCacheManager) {
        this.jenkinsTaskTrigger = jenkinsTaskTrigger;
        this.delayTaskCacheManager = delayTaskCacheManager;
    }

    @Override
    public void execute(ExecuteContext context) {
        final String projectKey = context.projectKey();
        //build trigger params
        Date time = context.startTime();
        TriggerContext triggerContext = new TriggerContext(context.env(), context.appName(),
            context.projectKey(), context.branch()).withTriggerDescribe(
            "This task is triggered manually");
        if (time == null) {
            //trigger now
            jenkinsTaskTrigger.trigger(triggerContext);
            log.info("Create a sonar scan task and trigger it once automatically,context:{}",
                context);
        } else {
            //delay trigger
            //执行时间距离当期系统时间的毫秒值
            final long execTime = calculateTime.applyAsLong(time);
            final TimeUnit unit = TimeUnit.MILLISECONDS;
            final long execTimeMilliseconds = unit.toMillis(execTime);
            final DelayedTaskContext oldTaskContext = delayTaskCacheManager.cached(projectKey);
            if (oldTaskContext != null) {
                //cancel old task
                oldTaskContext.future.cancel(true);
                log.info(
                    "You have created a future scan task for service {} at {}, now we need to cancel this task. Because you are creating a new task to execute after {} milliseconds at the moment. Without this mechanic, it would be pointless if you create multiple tasks with similar timings!",
                    projectKey, oldTaskContext.information(), execTimeMilliseconds);
            }
            ScheduledFuture<?> future = schedulePool.schedule(() -> {
                jenkinsTaskTrigger.trigger(triggerContext);
                //remove cache
                delayTaskCacheManager.remove(projectKey);
                log.debug("Delayed manual task has been executed");
            }, execTime, unit);
            //save||merge task
            delayTaskCacheManager.caching(projectKey,
                new DelayedTaskContext(execTimeMilliseconds, future));
            log.info(
                "Create a delayed sonar scan task which will be executed after {} milliseconds,context:{}",
                execTimeMilliseconds, context);
        }
    }
}
