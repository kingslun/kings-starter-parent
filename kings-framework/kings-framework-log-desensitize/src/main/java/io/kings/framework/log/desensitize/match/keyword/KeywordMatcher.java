package io.kings.framework.log.desensitize.match.keyword;

import io.kings.framework.log.desensitize.LogContext;
import io.kings.framework.log.desensitize.match.LogMatcher;
import io.kings.framework.log.desensitize.match.MatchResult;

/**
 * 日志匹配器
 *
 * @author lun.wang
 * @date 2021/12/21 1:46 PM
 * @since v1.1
 */
public final class KeywordMatcher implements LogMatcher {

    @Override
    public MatchResult match(LogContext context) {
        return new KeywordMatchResult(context);
    }
}
