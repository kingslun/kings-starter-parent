package io.kings.devops.backend.ci.auto.gitlab;

import static io.kings.devops.backend.ci.auto.Response.Status.GITLAB_WEBHOOK_CHECK_FAILURE;
import static io.kings.devops.backend.ci.auto.Response.Status.GITLAB_WEBHOOK_DELETE_FAILURE;
import static io.kings.devops.backend.ci.auto.Response.Status.GITLAB_WEBHOOK_PATCH_FAILURE;

import io.kings.devops.backend.ci.auto.BooleanUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.ProjectHook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * gitlabAPI
 *
 * @author lun.wang
 * @date 2022/3/25 6:53 PM
 * @since v2.5
 */
@Slf4j
@Component
class GitLabWebhookServiceImpl implements GitLabWebhookService {

    @Value("${gitlab.zeus-webhook}")
    private String zeusWebhook;
    private final ProjectApi projectApi;
    //兼容一个project多个webhook
    private final Map<String, WebhookObject> hooksMap = new ConcurrentHashMap<>(64);

    GitLabWebhookServiceImpl(ProjectApi projectApi) {
        this.projectApi = projectApi;
    }

    private String cachedKey(WebhookObject webhookObject) {
        return webhookObject.projectPath() + "-" + webhookObject.pushEventsBranchFilter();
    }

    //兼容其他机器创建了hook但本机内存无缓存的场景
    @Override
    public WebhookObject get(WebhookObject webhookObject, boolean cacheIfAbsent) {
        final String projectPath = webhookObject.projectPath();
        final String cachedKey = cachedKey(webhookObject);
        WebhookObject webhook = hooksMap.get(cachedKey);
        if (webhook != null) {
            return webhook;
        }
        try {
            List<ProjectHook> hooks = projectApi.getHooks(projectPath);
            if (CollectionUtils.isEmpty(hooks)) {
                return null;
            }
            for (ProjectHook hook : hooks) {//过滤不关注的hook
                if (!Objects.equals(zeusWebhook, hook.getUrl())) {
                    continue;
                }
                //绑定过分支说明设置了push事件则需要与当前分支做匹配
                //若分支相同则说明是关注的hook
                WebhookObject hook0 = matchHook(hook, projectPath,
                    webhookObject.pushEventsBranchFilter());
                if (hook0 != null) {
                    if (cacheIfAbsent) {
                        this.hooksMap.put(cachedKey, hook0);
                    }
                    return hook0;
                }
            }
            return null;
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == 404) {
                return null;
            }
            throw new GitlabException(GITLAB_WEBHOOK_CHECK_FAILURE, e);
        }
    }


    /**
     * 从gitlab的hook中匹配是否为指定的hook规则
     * <br>条件是：同一project可能有1~n个hook
     * <br>当绑定事件为`push且没有关联分支`、`非push` 时则为一个hook(因为这个hook是共用的 这是gitlab的机制)
     * <br>push事件且关联了分支则可创建多个hook
     *
     * @param hook        gitlab returned hook
     * @param projectPath project
     * @param branch      分支
     * @return 所以这里需要在不为一个hook时return null
     */
    private WebhookObject matchHook(ProjectHook hook, String projectPath, String branch) {
        String branch0 = hook.getPushEventsBranchFilter();
        return BooleanUtils.isTrue(hook.getPushEvents()) && StringUtils.hasText(branch0)
            && !Objects.equals(branch0, branch) ? null
            : new WebhookObject().projectPath(projectPath).hookId(hook.getId())
                .projectId(hook.getProjectId()).secretToken(hook.getToken())
                .enableSslVerification(hook.getEnableSslVerification())
                .enablePushEvents(hook.getPushEvents())
                .pushEventsBranchFilter(hook.getPushEventsBranchFilter())
                .enableMergeRequestEvents(hook.getMergeRequestsEvents());
    }

    private ProjectHook projectHook(WebhookObject request) {
        ProjectHook projectHook = new ProjectHook();
        if (request.enablePushEvents()) {
            projectHook.setPushEvents(true);
            projectHook.setPushEventsBranchFilter(request.pushEventsBranchFilter());
        } else {
            projectHook.setPushEvents(false);
            projectHook.setPushEventsBranchFilter(null);
        }
        projectHook.setMergeRequestsEvents(request.enableMergeRequestEvents());
        //api params
        projectHook.setToken(request.secretToken());
        projectHook.setUrl(zeusWebhook);
        projectHook.setEnableSslVerification(request.enableSslVerification());
        projectHook.setId(request.hookId());
        projectHook.setProjectId(request.projectId());
        return projectHook;
    }

    //save&merge webhook
    @Override
    public void patch(@NonNull WebhookObject request) {
        final String cachedKey = cachedKey(request);
        try {
            WebhookObject cached = get(request, true);
            if (cached != null) {
                request.hookId(cached.hookId());
                request.projectId(cached.projectId());
                //get from gitlab not hooksMap
                hooksMap.putIfAbsent(cachedKey, cached);
                //filtered out unnecessary modifications
                if (!Objects.equals(cached, request)) {
                    projectApi.modifyHook(projectHook(request));
                    //cover
                    hooksMap.put(cachedKey, request);
                    log.info("modified gitlab webhook:{}", request);
                }
            } else {
                ProjectHook projectHook = projectApi.addHook(request.projectPath(), zeusWebhook,
                    projectHook(request), request.enableSslVerification(), request.secretToken());
                //add
                final WebhookObject hook = request.hookId(projectHook.getId())
                    .projectId(projectHook.getProjectId());
                hooksMap.put(cachedKey, hook);
                log.info("added gitlab webhook:{}", hook);
            }
        } catch (GitLabApiException e) {
            throw new GitlabException(GITLAB_WEBHOOK_PATCH_FAILURE, e);
        }
    }

    @Override
    public void delete(WebhookObject webhookObject) {
        WebhookObject cached = get(webhookObject, false);
        if (cached != null) {
            try {
                this.hooksMap.remove(cachedKey(webhookObject));
                this.projectApi.deleteHook(cached.projectPath(), cached.hookId());
                log.info("deleted gitlab webhook {},it's no longer necessary", cached);
            } catch (GitLabApiException e) {
                throw new GitlabException(GITLAB_WEBHOOK_DELETE_FAILURE, e);
            }
        }
    }
}
