package io.kings.framework.log.desensitize.match.keyword;

import io.kings.framework.log.desensitize.LogContext;
import io.kings.framework.log.desensitize.StringAppenderFacade.StringAppender;
import io.kings.framework.log.desensitize.match.AbstractMatchResult;
import io.kings.framework.log.desensitize.match.MatchPair;
import io.kings.framework.log.desensitize.match.MatchResult;
import io.kings.framework.log.desensitize.match.Segment;
import io.kings.framework.log.desensitize.match.regular.Regular;
import io.kings.framework.log.desensitize.strategy.Strategy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * 默认的解析结果
 *
 * @author lun.wang
 * @date 2021/12/21 5:46 PM
 * @since v1.1
 */
final class KeywordMatchResult extends AbstractMatchResult implements MatchResult {

    //字符读取使用：当前读取位置
    private int cursor;
    private final StringAppender facade;

    static class StringAppenderFacade extends StringAppender {

        StringAppenderFacade(LogContext ctx) {
            super(ctx.currentVal());
        }
    }

    KeywordMatchResult(LogContext ctx) {
        super(ctx);
        facade = new StringAppenderFacade(ctx);
        super.analyze(this::readByCursor);
        this.shrink();
    }

    private void readByCursor(String keyword, Regular regular) {
        //How to parse strings efficiently? this is the problem,can you?
        while ((this.cursor = this.facade.source().indexOf(keyword, this.cursor)) >= 0) {
            //read depth? reject when pairs full
            if (super.pairCount + 1 > super.pairs.length) {
                return;
            }
            int beginIndex = this.cursor + keyword.length();
            Segment segment = new DefaultSegment(super.context, beginIndex, regular.getEndWith());
            //read value of this keyword
            super.pairs[super.pairCount++] = new CursorMatchPair(regular.getStrategy(),
                segment.beginIndex(), segment.endIndex());
            //read next
            this.cursor = segment.endIndex();
        }
        //reset cursor to 0 when read the tail by this keyword
        this.cursor = 0;
    }

    //Shrink
    private void shrink() {
        if (super.pairCount < super.pairs.length) {
            MatchPair[] newPairs = new MatchPair[super.pairCount];
            System.arraycopy(super.pairs, 0, newPairs, 0, super.pairCount);
            super.pairs = newPairs;
        }
    }

    //called finally
    @Override
    public String doResult() {
        if (this.facade.readable()) {
            return this.facade.after();
        }
        if (this.groupCount() > 0) {
            if (this.groupCount() > 1) {
                //sorted pairs
                Arrays.sort(super.pairs(), Comparator.comparingInt(Segment::beginIndex));
            }
            int offset0 = 0;
            for (MatchPair pair : super.pairs()) {
                int start = pair.beginIndex();
                int end = pair.endIndex();
                //latest legacy
                if (offset0 < start) {
                    this.facade.append(offset0, start);
                }
                Strategy strategy = pair.strategy();
                final boolean useIntern = super.context.useIntern();
                final String source = this.facade.source();
                offset0 = end;
                //replace pair to appender
                this.facade.append(super.security(strategy,
                    useIntern ? source.substring(start, end).intern()
                        : source.substring(start, end), useIntern));
            }
            //tail
            if (offset0 <= super.context.currentValLen()) {
                this.facade.append(offset0, super.context.currentValLen());
            }
        } else {
            this.facade.append(this.facade.source());
        }
        this.facade.flush();
        super.timeTakenToReady = this.watch();
        return this.facade.after();
    }

    @Override
    public String toString() {
        return "MatchResult{" + "message='" + this.facade.source() + "', result='" + (
            this.facade.readable() ? this.facade.after() : "[not resolved]")
            + "', timeTakenToReady='" + Optional.ofNullable(super.timeTakenToReady)
            .map(Duration::toMillis).orElse(null) + "'}";
    }
}
