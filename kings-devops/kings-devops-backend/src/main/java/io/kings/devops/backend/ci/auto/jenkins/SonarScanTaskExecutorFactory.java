package io.kings.devops.backend.ci.auto.jenkins;

import io.kings.devops.backend.ci.auto.gitlab.GitLabWebhookService;
import io.kings.devops.backend.ci.auto.openapi.vo.TaskType;

import java.util.EnumMap;
import java.util.Objects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * jenkins任务管理清偿
 *
 * @author lun.wang
 * @date 2022/3/25 1:45 PM
 * @since v2.5
 */
@Component
class SonarScanTaskExecutorFactory implements InitializingBean {

    final EnumMap<TaskType, SonarScanTaskExecutor> executors = new EnumMap<>(TaskType.class);
    private final JenkinsTaskTrigger jenkinsTaskTrigger;
    private final DelayTaskCacheManager delayTaskCacheManager;
    private final GitLabWebhookService gitLabWebHookService;

    SonarScanTaskExecutorFactory(JenkinsTaskTrigger jenkinsTaskTrigger,
                                 DelayTaskCacheManager delayTaskCacheManager, GitLabWebhookService gitLabWebHookService) {
        this.jenkinsTaskTrigger = jenkinsTaskTrigger;
        this.delayTaskCacheManager = delayTaskCacheManager;
        this.gitLabWebHookService = gitLabWebHookService;
    }

    @Override
    public void afterPropertiesSet() {
        executors.put(TaskType.GITLAB_EVENT,
                new GitLabEventSonarScanTaskExecutor(gitLabWebHookService));
        executors.put(TaskType.MANUAL,
                new ManualSonarScanTaskExecutor(jenkinsTaskTrigger, delayTaskCacheManager));
        executors.put(TaskType.SCHEDULE, new ScheduleSonarScanTaskExecutor());
    }

    void invoke(@NonNull ExecuteContext context) {
        Objects.requireNonNull(executors.get(context.taskType())).execute(context);
    }
}
