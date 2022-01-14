package io.kings.framework.log.desensitize;

import java.time.Duration;

/**
 * 计时器
 *
 * @author lun.wang
 * @date 2021/12/28 4:50 PM
 * @since v1.2
 */
@FunctionalInterface
public interface StopWatch {

    Duration watch();
}
