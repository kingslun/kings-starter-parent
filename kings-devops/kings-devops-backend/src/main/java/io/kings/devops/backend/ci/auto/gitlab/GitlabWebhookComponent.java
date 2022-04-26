package io.kings.devops.backend.ci.auto.gitlab;

import io.kings.devops.backend.ci.auto.jenkins.JenkinsTaskTrigger;
import io.kings.devops.backend.ci.auto.jenkins.JenkinsTaskTrigger.TriggerContext;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookRequestDto;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookRequestDto.Kind;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookRequestDto.MergeState;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookResponseDto;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookResponseDto.State;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanDo;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanRepository;
import java.util.function.BinaryOperator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 处理gitlab服务器回调的组件
 *
 * @author lun.wang
 * @date 2022/3/29 2:45 PM
 * @since v2.5
 */
@Slf4j
@Component
@AllArgsConstructor
public class GitlabWebhookComponent {

    private final JenkinsTaskSonarScanRepository sonarScanTaskComponent;
    private final JenkinsTaskTrigger jenkinsTaskTrigger;
    private final BinaryOperator<String> describe = (event, user) -> String.format(
        "This task is triggered by the GitLab user %s %s", user, event);

    private void validParams(WebhookRequestDto requestDto) throws ValidException {
        if (requestDto.getProject() == null) {
            throw new ValidException(State.HOOK_WITHOUT_PROJECT);
        }
        if (!StringUtils.hasText(requestDto.getProject().getPathWithNamespace())) {
            throw new ValidException(State.HOOK_WITHOUT_PROJECT_NAME);
        }
        if (Kind.PUSH == requestDto.getKind() && !StringUtils.hasText(requestDto.getBranch())) {
            throw new ValidException(State.PUSH_WITHOUT_BRANCH);
        }
        if (Kind.MERGE_REQUEST == requestDto.getKind()) {
            if (requestDto.getObjectAttributes() == null) {
                throw new ValidException(State.MERGE_WITHOUT_MEDIA_DATA);
            }
            if (requestDto.getUser() == null) {
                throw new ValidException(State.MERGE_WITHOUT_USER_INFO);
            }
            if (!StringUtils.hasText(requestDto.getObjectAttributes().getTargetBranch())) {
                throw new ValidException(State.MERGE_WITHOUT_TARGET_BRANCH);
            }
        }
    }

    public WebhookResponseDto onHook(WebhookRequestDto requestDto) {
        log.debug("on gitlab webhook:{}", requestDto);
        final String gitlabProjectPath = requestDto.getProject().getPathWithNamespace();
        try {
            validParams(requestDto);
            String branch;
            String user;
            Kind kind = requestDto.getKind();
            switch (kind) {
                case PUSH:
                    branch = requestDto.getBranch();
                    user = requestDto.getGitUser();
                    break;
                case MERGE_REQUEST:
                    //triggered jenkins by merge events & Now filtered out events like Open, Approval, Close...
                    if (MergeState.MERGED != requestDto.getObjectAttributes().getState()) {
                        return WebhookResponseDto.of(true,
                            State.HOOK_FILTERED_BY_UNENFORCED_MERGE_STATES);
                    }
                    branch = requestDto.getObjectAttributes().getTargetBranch();
                    user = requestDto.getUser().getUsername();
                    break;
                case OTHER:
                default:
                    throw new UnsupportedOperationException();
            }
            if (!StringUtils.hasText(gitlabProjectPath)) {
                throw new ValidException(State.HOOK_WITHOUT_PROJECT_PATH);
            }
            if (!StringUtils.hasText(branch)) {
                throw new ValidException(State.HOOK_WITHOUT_BRANCH);
            }
            if (!StringUtils.hasText(user)) {
                throw new ValidException(State.HOOK_WITHOUT_USER);
            }
            JenkinsTaskSonarScanDo taskDo = sonarScanTaskComponent.findByGitlabProjectPathAndBranch(
                gitlabProjectPath, branch);
            if (taskDo == null) {
                return WebhookResponseDto.of(false, State.HOOK_TRIGGERED_WITHOUT_TASK_DATA);
            }
            //triggered jenkins by events
            jenkinsTaskTrigger.trigger(
                new TriggerContext(taskDo.getEnv(), taskDo.getAppName(), taskDo.getProjectKey(),
                    branch).withGit(taskDo.getGitlabUrl(), taskDo.getGitlabJenkinsCredentialsId())
                    .withTriggerDescribe(describe.apply(requestDto.getKind().getPrint(), user)));
            log.info("Gitlab webhook triggered success. origin:{}", taskDo);
            return WebhookResponseDto.of(true, State.HOOK_TRIGGERED_JENKINS_TASK_SUCCESS);
        } catch (ValidException e) {
            log.warn("Valid gitlab webhook request params failure", e);
            return WebhookResponseDto.of(true, e.state);
        } catch (Exception e) {
            log.warn("Gitlab webhook triggered failure", e);
            return WebhookResponseDto.of(true, State.HOOK_TRIGGERED_JENKINS_TASK_ERROR);
        }
    }

    @AllArgsConstructor
    static class ValidException extends Exception {

        final State state;
    }
}
