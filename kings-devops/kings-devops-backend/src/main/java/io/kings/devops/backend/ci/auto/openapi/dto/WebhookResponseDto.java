package io.kings.devops.backend.ci.auto.openapi.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

/**
 * 回应gitlab的数据
 *
 * @author lun.wang
 * @date 2022/3/29 2:40 PM
 * @since v2.5
 */
@Getter
@Setter
@ToString
public class WebhookResponseDto implements Serializable {

    @NonNull
    private Boolean triggeredJenkinsTask;
    @NonNull
    private State state;

    public static WebhookResponseDto of(boolean triggered, @NonNull State state) {
        WebhookResponseDto responseDto = new WebhookResponseDto();
        responseDto.setTriggeredJenkinsTask(triggered);
        responseDto.setState(state);
        return responseDto;
    }

    //triggered jenkins task statues
    public enum State {
        HOOK_WITHOUT_PROJECT,
        HOOK_WITHOUT_PROJECT_NAME,
        PUSH_WITHOUT_BRANCH,
        MERGE_WITHOUT_USER_INFO,
        MERGE_WITHOUT_MEDIA_DATA,
        MERGE_WITHOUT_TARGET_BRANCH,
        HOOK_WITHOUT_PROJECT_PATH,
        HOOK_WITHOUT_BRANCH,
        HOOK_WITHOUT_USER,
        HOOK_TRIGGERED_JENKINS_TASK_SUCCESS,
        HOOK_TRIGGERED_JENKINS_TASK_FAILURE,
        HOOK_TRIGGERED_JENKINS_TASK_ERROR,
        HOOK_TRIGGERED_WITHOUT_TASK_DATA,
        HOOK_FILTERED_BY_UNENFORCED_MERGE_STATES
    }
}
