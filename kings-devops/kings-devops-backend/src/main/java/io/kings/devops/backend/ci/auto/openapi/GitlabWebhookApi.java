package io.kings.devops.backend.ci.auto.openapi;

import io.kings.devops.backend.ci.auto.Response;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookRequestDto;
import io.kings.devops.backend.ci.auto.openapi.dto.WebhookResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * gitlab回调接口
 *
 * @author lun.wang
 * @date 2022/3/25 7:18 PM
 * @since v2.5
 */
@RequestMapping("/api/v1/gitlab")
public interface GitlabWebhookApi {

    @PostMapping("/webhook/call")
    Response<WebhookResponseDto> onHook(@RequestBody WebhookRequestDto requestDto);
}
