package io.kings.framework.log.desensitize;

import lombok.AllArgsConstructor;

/**
 * 脱敏上下文
 *
 * @author lun.wang
 * @date 2022/1/10 2:29 PM
 * @since v1.2
 */
@FunctionalInterface
public interface DesensitizeContext {

    /**
     * 当前处理字符串
     *
     * @return source value
     */
    String currentVal();

    default boolean currentValEmpty() {
        return ObjectUtils.isEmpty(this.currentVal());
    }

    default int currentValLen() {
        this.currentValRequired();
        return this.currentVal().length();
    }

    default void currentValRequired() {
        if (this.currentValEmpty()) {
            throw new DesensitizeException("current value required not be empty");
        }
    }

    @AllArgsConstructor
    class DefaultContext implements DesensitizeContext {

        private final String currentVal;

        @Override
        public String currentVal() {
            return this.currentVal;
        }
    }
}
