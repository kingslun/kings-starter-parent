package io.kings.devops.backend.ci.auto.openapi.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 选择了gitlab事件触发扫描任务的配置
 *
 * @author lun.wang
 * @date 2022/3/28 5:04 PM
 * @since v2.5
 */
@Getter
@Setter
@ToString
public class GitlabWebhookVo implements Serializable {

    private String secretToken;
    private Boolean enableSslVerification;
    private Boolean enablePushEvents;
    private Boolean enableMergeRequestEvents;
}
