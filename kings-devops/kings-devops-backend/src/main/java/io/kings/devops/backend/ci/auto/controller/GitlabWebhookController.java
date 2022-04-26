package io.kings.devops.backend.ci.auto.controller;

import io.kings.devops.backend.ci.auto.Response;
import io.kings.devops.backend.ci.auto.Response.Status;
import io.kings.devops.backend.ci.auto.gitlab.GitlabWebhookComponent;
import io.kings.devops.backend.ci.auto.openapi.GitlabWebhookApi;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookRequestDto;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * gitlab webhook回调处理 过滤mr过程中的重复调用
 * <br>mr分为open/update/close/accept等多种阶段
 * 目前处理accept阶段==>在合并请求呗同意后触发合并代码的扫描
 * <br>如果需要考虑到各阶段的扫描请讨论后提需求
 *
 * @author lun.wang
 * @date 2022/3/22 2:01 PM
 * @since v2.5
 */
@Slf4j
@RestController
@AllArgsConstructor
public class GitlabWebhookController implements GitlabWebhookApi {

    private final GitlabWebhookComponent gitlabWebhookComponent;

    //webhook trigger sonar scan task
    @Override
    public Response<WebhookResponseDto> onHook(@RequestBody WebhookRequestDto requestDto) {
        return Response.of(Status.SUCCESS, gitlabWebhookComponent.onHook(requestDto));
    }
}
