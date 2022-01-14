package io.kings.framework.log.desensitize.match.regexp;

import io.kings.framework.log.desensitize.LogContext;
import io.kings.framework.log.desensitize.match.LogMatcher;
import io.kings.framework.log.desensitize.match.MatchResult;

/**
 * 正则日志匹配器
 *
 * @author lun.wang
 * @date 2021/12/21 1:46 PM
 * @since v1.1
 */
public final class RegexpMatcher implements LogMatcher {


    @Override
    public MatchResult match(LogContext ctx) {
        return new RegExpMatchResult(ctx);
    }
}
