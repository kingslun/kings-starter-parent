package io.kings.devops.backend.ci.auto.gitlab;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 创建gitlab webhook的参数
 *
 * @author lun.wang
 * @date 2022/3/28 1:39 PM
 * @since v2.5
 */
@Getter
@Setter
@Accessors(fluent = true)
public class WebhookObject implements Serializable {

    private String projectPath;
    //Use this token to validate received payloads. It is sent with the request in the X-Gitlab-Token HTTP header.
    private String secretToken;
    private boolean enableSslVerification;//ssl验证

    //gitlab common events params
    private boolean enablePushEvents;//推送事件
    private String pushEventsBranchFilter;//推送事件受检分支
    private boolean enableMergeRequestEvents;//合并事件

    //gitlab response
    private Long hookId;
    private Long projectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookObject)) {
            return false;
        }
        WebhookObject that = (WebhookObject) o;
        return Objects.equals(hookId, that.hookId) && Objects.equals(projectId, that.projectId)
                && Objects.equals(projectPath, that.projectPath)
                && enableSslVerification == that.enableSslVerification
                && enablePushEvents == that.enablePushEvents
                && enableMergeRequestEvents == that.enableMergeRequestEvents && Objects.equals(
                secretToken, that.secretToken) && Objects.equals(pushEventsBranchFilter,
                that.pushEventsBranchFilter);
    }

    @Override
    public String toString() {
        return "WebhookObject{" + "projectPath='" + projectPath + "', secretToken='" + secretToken
                + "', enableSslVerification=" + enableSslVerification + ", enablePushEvents="
                + enablePushEvents + ", pushEventsBranchFilter='" + pushEventsBranchFilter
                + "', enableMergeRequestEvents=" + enableMergeRequestEvents + ", hookId=" + hookId
                + ", projectId=" + projectId + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectPath, secretToken, enableSslVerification, enablePushEvents,
                pushEventsBranchFilter, enableMergeRequestEvents, hookId, projectId);
    }

    //================================feature events================================
    private boolean enableTagPushEvents;//URL is triggered when a new tag is pushed to the repository
    private boolean enableCommentsEvents;//URL is triggered when someone adds a comment
    private boolean enableConfidentialCommentsEvents;//URL is triggered when someone adds a comment on a confidential issue
    private boolean enableIssuesEvents;//URL is triggered when an issue is created, updated, closed, or reopened
    private boolean enableConfidentialIssuesEvents;//URL is triggered when a confidential issue is created, updated, closed, or reopened
    private boolean enableJobEvents;//URL is triggered when the job status changes
    private boolean enablePipelineEvents;//URL is triggered when the pipeline status changes
    private boolean enableWikiPageEvents;//URL is triggered when a wiki page is created or updated
    private boolean enableDeploymentEvents;//URL is triggered when a deployment starts, finishes, fails, or is canceled
    private boolean enableFeatureFlagEvents;//URL is triggered when a feature flag is turned on or off
    private boolean enableReleasesEvents;//URL is triggered when a release is created or updated
}
