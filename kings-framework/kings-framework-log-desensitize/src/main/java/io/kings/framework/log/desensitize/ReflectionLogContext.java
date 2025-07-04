package io.kings.framework.log.desensitize;

import lombok.AllArgsConstructor;

import java.lang.reflect.Field;

/**
 * 反射处理日志上下文对象
 *
 * @author lun.wang
 * @date 2021/12/20 11:08 AM
 * @since v1.1
 */
@AllArgsConstructor
class ReflectionLogContext implements DesensitizeContext {

    private final Object source;
    private final Field field;

    @Override
    public String currentVal() {
        try {
            return String.valueOf(field.get(source));
        } catch (IllegalAccessException e) {
            return "";
        }
    }
}
