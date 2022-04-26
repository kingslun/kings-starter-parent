package io.kings.devops.backend.ci.auto.gitlab;

import org.springframework.lang.NonNull;

/**
 * gitlabAPI
 *
 * @author lun.wang
 * @date 2022/3/25 6:53 PM
 * @since v2.5
 */
public interface GitLabWebhookService {

    //check hook
    WebhookObject get(@NonNull WebhookObject webhookObject, boolean cacheIfAbsent);

    //save&merge webhook
    void patch(@NonNull WebhookObject webhook);

    //从event转入其他类型的任务时需要移除webhook
    void delete(@NonNull WebhookObject webhookObject);
}
