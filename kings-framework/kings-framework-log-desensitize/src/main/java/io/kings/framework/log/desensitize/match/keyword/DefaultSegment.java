package io.kings.framework.log.desensitize.match.keyword;


import io.kings.framework.log.desensitize.LogContext;
import io.kings.framework.log.desensitize.match.Segment;

/**
 * 默认的脱敏字符段截取实现
 *
 * @author lun.wang
 * @date 2021/12/28 4:24 PM
 * @since v1.2
 */
class DefaultSegment implements Segment {

    private final LogContext ctx;
    private final int beginIndex;
    private Integer endIndex;
    private final char[] cutoffs;

    DefaultSegment(LogContext ctx, int offset, char[] cutoffs) {
        this.ctx = ctx;
        this.beginIndex = offset + 1;
        this.cutoffs = cutoffs;
    }

    @Override
    public boolean skipped(char c) {
        for (char cc : this.cutoffs) {
            if (cc == c) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int beginIndex() {
        return this.beginIndex;
    }

    @Override
    public int endIndex() {
        if (this.endIndex == null) {
            int end = this.beginIndex();
            final int len = this.ctx.currentValLen();
            final String src = this.ctx.currentVal();
            while (end < len && !this.skipped(src.charAt(end))) {
                end++;
            }
            //空格处理
            this.endIndex = end;
        }
        return this.endIndex;
    }
}
