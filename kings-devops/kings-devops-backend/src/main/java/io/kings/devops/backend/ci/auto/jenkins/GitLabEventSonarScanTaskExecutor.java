package io.kings.devops.backend.ci.auto.jenkins;

import io.kings.devops.backend.ci.auto.gitlab.GitLabWebhookService;
import lombok.extern.slf4j.Slf4j;

/**
 * gitlab事件任务执行器 根据上下文创建gitlab的webhook 涉及对接gitlab的API
 *
 * @author lun.wang
 * @date 2022/3/25 2:38 PM
 * @since v2.5
 */
@Slf4j
class GitLabEventSonarScanTaskExecutor implements SonarScanTaskExecutor {

    private final GitLabWebhookService gitLabWebHookService;

    GitLabEventSonarScanTaskExecutor(GitLabWebhookService gitLabWebHookService) {
        this.gitLabWebHookService = gitLabWebHookService;
    }

    @Override
    public void execute(ExecuteContext context) {
        //考虑Git事件 需要调用gitlab api创建webhook
        gitLabWebHookService.patch(context.webhook());
        log.info("Create a gitlab event scan task with context:{}", context);
    }
}
