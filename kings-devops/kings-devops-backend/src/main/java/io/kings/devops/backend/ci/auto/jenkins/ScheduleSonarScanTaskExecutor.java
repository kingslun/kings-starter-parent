package io.kings.devops.backend.ci.auto.jenkins;

import lombok.extern.slf4j.Slf4j;

/**
 * jenkins定时任务
 * <br>Jenkins定时任务不再开发者考虑范围内，or do nothing
 *
 * @author lun.wang
 * @date 2022/3/25 2:38 PM
 * @since v2.5
 */
@Slf4j
class ScheduleSonarScanTaskExecutor implements SonarScanTaskExecutor {

    @Override
    public void execute(ExecuteContext context) {
        //log
        log.info("Create a scheduled scan task with context:{}, cron express:[{}]", context,
            context.cron());
    }
}
