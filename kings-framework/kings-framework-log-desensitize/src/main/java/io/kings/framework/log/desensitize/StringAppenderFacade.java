package io.kings.framework.log.desensitize;

import java.text.MessageFormat;

/**
 * 字符串增强器
 * <br>
 * feature task: 1. appender
 *
 * @author lun.wang
 * @date 2022/1/10 1:40 PM
 * @since v1.2
 */
public interface StringAppenderFacade {

    char CHAR_STAR = '*';
    char CHAR_AT = '@';

    String source();

    StringAppenderFacade append(int start, int end);

    StringAppenderFacade append(String append);

    StringAppenderFacade append(char append);

    StringAppenderFacade appendStar(int len);

    String after();

    boolean readable();

    StringAppenderFacade flush();

    abstract class StringAppender implements StringAppenderFacade {

        //是否可读
        private volatile boolean readable;
        private final String source;
        private final StringBuilder sb = new StringBuilder();

        protected StringAppender(String source) {
            this.source = source;
        }

        public final String source() {
            return this.source;
        }

        @Override
        public boolean readable() {
            return this.readable;
        }

        @Override
        public StringAppenderFacade flush() {
            this.readable = true;
            return this;
        }

        /**
         * fluent拼接
         *
         * @param len 填充长度
         * @return appender
         */
        public StringAppenderFacade appendStar(int len) {
            if (len < 0) {
                throw new io.kings.framework.log.desensitize.DesensitizeException(
                    MessageFormat.format("illegal arguments [len:{0}]", len));
            }
            for (int i = 0; i < len; i++) {
                this.append(CHAR_STAR);
            }
            return this;
        }

        public StringAppenderFacade append(int start, int end) {
            if (start < 0 || start > end) {
                throw new io.kings.framework.log.desensitize.DesensitizeException(
                    MessageFormat.format("illegal arguments [start:{0},end:{1}]", start, end));
            }
            for (int i = start; i < end; i++) {
                this.sb.append(this.source.charAt(i));
            }
            return this;
        }

        @Override
        public StringAppenderFacade append(String append) {
            this.sb.append(append);
            return this;
        }

        @Override
        public StringAppenderFacade append(char append) {
            this.sb.append(append);
            return this;
        }

        @Override
        public String after() {
            if (this.readable) {
                return this.sb.toString();
            }
            throw new AppenderException("Not readable");
        }
    }

    class AppenderException extends io.kings.framework.log.desensitize.DesensitizeException {

        public AppenderException(String message) {
            super(message);
        }
    }
}
