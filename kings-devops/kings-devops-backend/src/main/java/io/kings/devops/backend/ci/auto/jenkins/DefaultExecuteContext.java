package io.kings.devops.backend.ci.auto.jenkins;

import io.kings.devops.backend.ci.auto.DateTimeFormatter;
import io.kings.devops.backend.ci.auto.ProjectKeyGenerator;
import io.kings.devops.backend.ci.auto.ProjectKeyGenerator.ProjectKey;
import io.kings.devops.backend.ci.auto.gitlab.WebhookObject;
import io.kings.devops.backend.ci.auto.openapi.vo.CreateSonarScanTaskRequestVo;
import io.kings.devops.backend.ci.auto.openapi.vo.TaskType;
import java.util.Date;
import java.util.Objects;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 默认的上下文实现
 *
 * @author lun.wang
 * @date 2022/3/25 3:29 PM
 * @since v2.5
 */
class DefaultExecuteContext extends ProjectKey implements ExecuteContext {

    @NonNull
    private final Date startTime;
    @Nullable
    private final String cron;
    @Nullable
    private WebhookObject webhook;
    @NonNull
    private final TaskType taskType;

    private DefaultExecuteContext(CreateSonarScanTaskRequestVo requestVo) {
        super(requestVo.getEnv(), requestVo.getAppName(), requestVo.getBranch());
        this.startTime = requestVo.getStartTime();
        this.cron = requestVo.getCron();
        this.taskType = requestVo.getTaskType();
    }

    static DefaultExecuteContext from(CreateSonarScanTaskRequestVo requestVo) {
        return new DefaultExecuteContext(requestVo);
    }

    @Override
    public String env() {
        return super.getEnv();
    }

    @Override
    public String cron() {
        return cron;
    }

    @NonNull
    @Override
    public WebhookObject webhook() {
        Assert.notNull(this.webhook, "webhook config is null");
        return webhook;
    }

    @Override
    public void setWebhook(@NonNull WebhookObject webhook) {
        Assert.notNull(webhook, "illegal webhook params");
        this.webhook = webhook;
    }

    @NonNull
    @Override
    public TaskType taskType() {
        return Objects.requireNonNull(taskType);
    }

    @Override
    public String appName() {
        return super.getAppName();
    }

    @Override
    public String branch() {
        return super.getBranch();
    }

    @Override
    public Date startTime() {
        return this.startTime;
    }

    @Override
    public String toString() {
        return "[TaskContext:{project_name:" + super.toString() + ",startTime:"
            + DateTimeFormatter.DEFAULT.format(startTime) + "}]";
    }

    @Override
    public String projectKey() {
        return ProjectKeyGenerator.DEFAULT.projectKey(this.env(), this.appName(), this.branch());
    }
}
