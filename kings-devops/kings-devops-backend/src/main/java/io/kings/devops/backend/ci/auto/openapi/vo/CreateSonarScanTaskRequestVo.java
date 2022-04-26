package io.kings.devops.backend.ci.auto.openapi.vo;


import static io.kings.devops.backend.ci.auto.DateTimeFormatter.DATE_PATTERN;
import static io.kings.devops.backend.ci.auto.DateTimeFormatter.TIMEZONE;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lun.wang
 * @date 2022/3/21 3:56 PM
 * @since v2.5
 */
@Getter
@Setter
@ToString
public class CreateSonarScanTaskRequestVo implements Serializable {

    private String env;

    @NotBlank(message = "服务名称不能为空")
    private String appName;

    @NotBlank(message = "扫描分支不能为空")
    private String branch;

    private String rootPomPath;

    @NotNull(message = "扫描任务类型不能为空")
    private TaskType taskType;

    private String taskDescription;
    /**
     * only schedule task
     *
     * @see TaskType#SCHEDULE
     */
    private String cron;

    /**
     * only gitlab_event task
     *
     * @see TaskType#GITLAB_EVENT
     */
    private GitlabWebhookVo webhook;

    /**
     * 开始扫描时间 默认此刻【当前提交时间为准】 如果有值则说明希望按照指定时间来执行
     */
    @JsonFormat(pattern = DATE_PATTERN, timezone = TIMEZONE)
    private Date startTime;
}
