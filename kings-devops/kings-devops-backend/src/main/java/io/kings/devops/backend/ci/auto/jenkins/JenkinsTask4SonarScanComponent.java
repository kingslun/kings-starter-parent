package io.kings.devops.backend.ci.auto.jenkins;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import io.kings.devops.backend.ci.auto.AutoCiException;
import io.kings.devops.backend.ci.auto.Response.Status;
import io.kings.devops.backend.ci.auto.config.ConfigurationManager;
import io.kings.devops.backend.ci.auto.gitlab.GitLabWebhookService;
import io.kings.devops.backend.ci.auto.gitlab.WebhookObject;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanDo;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanDo.DeleteState;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanRepository;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * jenkins任务实现
 *
 * @author lun.wang
 * @date 2022/3/22 3:20 PM
 * @since v2.5
 */
@Slf4j
@Component
public class JenkinsTask4SonarScanComponent extends AbstractJenkinsTask4SonarScan {

    private final SonarScanTaskExecutorFactory sonarScanTaskExecutorFactory;

    JenkinsTask4SonarScanComponent(JenkinsTaskSonarScanRepository sonarScanTaskComponent,
        ConfigurationManager configurationManager,
        SonarScanTaskExecutorFactory sonarScanTaskExecutorFactory,
        DelayTaskCacheManager delayTaskCacheManager, GitLabWebhookService gitLabWebhookService) {
        super(sonarScanTaskComponent, configurationManager, delayTaskCacheManager,
            gitLabWebhookService);
        this.sonarScanTaskExecutorFactory = sonarScanTaskExecutorFactory;
    }

    @Override
    protected void doCreateSonarScanTask(ExecuteContext context) {
        //execute job
        this.sonarScanTaskExecutorFactory.invoke(context);
    }

    private JenkinsTaskSonarScanDo queryAndValid(String projectKey) {
        JenkinsTaskSonarScanDo task = repository.findByProjectKey(projectKey);
        if (task == null || DeleteState.YES == DeleteState.of(task.getIsDelete())) {
            throw new AutoCiException(Status.FAILURE, "project:" + projectKey + " notfound");
        }
        return task;
    }

    @Override
    public void deleteStaticScanTask(String projectKey) {
        JenkinsTaskSonarScanDo task = queryAndValid(projectKey);
        //若后续继续对projectKey操作需要启用
        repository.deleteByProjectKey(projectKey);
        //delete jenkins job
        try {
            configurationManager.jenkinsServer(task.getEnv()).deleteJob(projectKey);
        } catch (IOException e) {
            throw new JenkinsException(Status.JENKINS_TASK_PATCH_ERROR, "delete task failure");
        }
        //cancel delay task
        cancelDelayTaskIfPresent(projectKey);
        //remove webhook
        clearGitlabWebhookIfPresent(new WebhookObject().projectPath(task.getGitlabProjectPath())
            .pushEventsBranchFilter(task.getBranch()));
    }

    @Override
    public String latestStaticScanTaskConsoleLog(String projectKey) {
        JenkinsTaskSonarScanDo task = queryAndValid(projectKey);
        try {
            JobWithDetails job = configurationManager.jenkinsServer(task.getEnv())
                .getJob(projectKey);
            if (job == null) {
                throw new JenkinsException(Status.JENKINS_TASK_NOTFOUND);
            }
            Build build = job.getLastBuild();
            if (build == null) {
                throw new JenkinsException(Status.JENKINS_TASK_NOTFOUND, "not built");
            }
            BuildWithDetails details = build.details();
            if (details == null) {
                throw new JenkinsException(Status.JENKINS_TASK_NOTFOUND, "without build details");
            }
            return details.getConsoleOutputHtml();
        } catch (IOException e) {
            throw new JenkinsException(Status.JENKINS_TASK_CONSOLE_LOG_FAILURE);
        }
    }
}
