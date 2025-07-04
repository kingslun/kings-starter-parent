package io.kings.framework.log.desensitize.match;

import io.kings.framework.log.desensitize.DesensitizeException;
import io.kings.framework.log.desensitize.StopWatch;
import io.kings.framework.log.desensitize.strategy.Strategy;

/**
 * 匹配结果描述对象 用于描述以下字符
 * <br>{@link this#groupCount()} 1
 * <br>{@link this#pairs()} Pair{key='name',value='张三丰'}
 * 根据上述 经过解析源字符串会变成一个个的pair描述符号 最终根据appender+pairs合并变成最终的解析后字符串
 *
 * @author lun.wang
 * @date 2021/12/21 3:01 PM
 * @since v1.1
 */
public interface MatchResult extends StopWatch {

    String result();

    /**
     * 匹配次数
     */
    int groupCount();

    MatchPair[] pairs();

    class CursorMatchPair implements MatchPair {

        private final Strategy strategy;
        private final int beginIndex;
        private final int endIndex;

        public CursorMatchPair(Strategy strategy, int beginIndex, int endIndex) {
            if (beginIndex < 0 || beginIndex > endIndex) {
                throw new DesensitizeException("illegal arguments for Pair");
            }
            this.strategy = strategy;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }

        @Override
        public Strategy strategy() {
            return this.strategy;
        }

        @Override
        public int beginIndex() {
            return this.beginIndex;
        }

        @Override
        public int endIndex() {
            return this.endIndex;
        }

        @Override
        public String toString() {
            return String.format("Pair{keyword='%s',beginIndex='%s',endIndex='%s'}",
                    this.strategy.getKeyword(), this.beginIndex, this.endIndex);
        }
    }
}
