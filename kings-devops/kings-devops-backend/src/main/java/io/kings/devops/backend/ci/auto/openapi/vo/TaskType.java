package io.kings.devops.backend.ci.auto.openapi.vo;

/**
 * 静态代码扫描任务类型
 *
 * @author lun.wang
 * @date 2022/3/21 4:15 PM
 * @since v2.5
 */

public enum TaskType {
    /**
     * 手动任务
     */
    MANUAL,
    /**
     * 定时任务
     */
    SCHEDULE,
    /**
     * gitlab时间任务
     */
    GITLAB_EVENT
}
