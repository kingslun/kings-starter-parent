package io.kings.devops.backend.ci.auto.jenkins;

import io.kings.devops.backend.ci.auto.gitlab.WebhookObject;
import io.kings.devops.backend.ci.auto.openapi.vo.TaskType;

import java.util.Date;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 执行上下文
 *
 * @author lun.wang
 * @date 2022/3/25 2:32 PM
 * @since v2.5
 */
interface ExecuteContext {

    String env();

    String appName();

    String branch();

    String projectKey();

    Date startTime();

    //only schedule
    @Nullable
    String cron();

    //only gitlab event
    @NonNull
    WebhookObject webhook();

    void setWebhook(@NonNull WebhookObject webhook);

    @NonNull
    TaskType taskType();
}
