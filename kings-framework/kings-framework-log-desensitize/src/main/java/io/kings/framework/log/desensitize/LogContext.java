package io.kings.framework.log.desensitize;

/**
 * 脱敏上下文
 *
 * @author lun.wang
 * @date 2021/12/20 11:50 AM
 * @since v1.1
 */
public interface LogContext extends io.kings.framework.log.desensitize.DesensitizeContext {

    /**
     * 匹配深度
     *
     * @return depth
     */
    int matchDepth();

    /**
     * 字符串缓存池
     *
     * @return true/false
     */
    boolean useIntern();

    class SensitiveLogContext extends DefaultContext implements LogContext {

        public SensitiveLogContext(String currentVal, int depth, boolean useIntern) {
            super(currentVal);
            this.depth = depth;
            this.useIntern = useIntern;
        }

        private final int depth;
        private final boolean useIntern;

        @Override
        public boolean useIntern() {
            return this.useIntern;
        }

        @Override
        public int matchDepth() {
            return this.depth;
        }
    }
}
