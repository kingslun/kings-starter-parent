package io.kings.devops.backend.ci.auto.jenkins;

/**
 * Jenkins扫描任务处理器
 *
 * @author lun.wang
 * @date 2022/3/25 1:48 PM
 * @since v2.5
 */
interface SonarScanTaskExecutor {

    void execute(ExecuteContext context);
}
