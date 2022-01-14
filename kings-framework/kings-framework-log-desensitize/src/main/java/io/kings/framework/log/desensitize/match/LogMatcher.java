package io.kings.framework.log.desensitize.match;


import io.kings.framework.log.desensitize.LogContext;

/**
 * 日志匹配器
 *
 * @author lun.wang
 * @date 2021/12/21 1:46 PM
 * @since v1.1
 */
@FunctionalInterface
public interface LogMatcher {

    MatchResult match(LogContext logContext);
}
