package io.kings.framework.log.desensitize.match.regexp;

import io.kings.framework.log.desensitize.LogContext;
import io.kings.framework.log.desensitize.match.AbstractMatchResult;
import io.kings.framework.log.desensitize.match.MatchResult;
import io.kings.framework.log.desensitize.match.regular.Regular;
import io.kings.framework.log.desensitize.strategy.Strategy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则脱敏匹配器
 * <font color=yellow>
 * <br>feature task:
 * <br>1.正则匹配日志
 * <br>2.脱敏敏感信息
 * <br>3.打印脱敏后的日志
 * </font>
 *
 * @author lun.wang
 * @date 2021/12/29 2:41 PM
 * @since v1.2
 */
final class RegExpMatchResult extends AbstractMatchResult implements MatchResult {

    RegExpMatchResult(LogContext ctx) {
        super(ctx);
        super.analyze(this::read);
    }

    private String result;

    private void read(String keyword, Regular regular) {
        Strategy strategy = regular.getStrategy();
        Pattern pattern = Pattern.compile(strategy.getPattern());
        String src = this.result == null ? super.context.currentVal() : this.result;
        Matcher matcher = pattern.matcher(src);
        int i = 0;
        final int depth = super.context.matchDepth();
        while (i < depth && matcher.find()) {
            int valueStart = matcher.start();
            int valueEnd = matcher.end();
            if (valueStart < 0 || valueEnd < 0) {
                break;
            }
            boolean useIntern = super.context.useIntern();
            result = matcher.replaceFirst(
                super.security(strategy, useIntern ? matcher.group().intern() : matcher.group(),
                    useIntern));
            i++;
        }
    }

    @Override
    public String doResult() {
        return this.result;
    }
}
