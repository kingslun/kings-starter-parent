package io.kings.framework.log.desensitize;

import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * 简单密码脱敏
 *
 * @author lun.wang
 * @date 2021/12/20 11:06 AM
 * @since v1.1
 */
@AllArgsConstructor
public final class FooPasswordCondition implements IogCondition {

    private final String fooPassword;

    @Override
    public boolean valid(DesensitizeContext ctx) {
        return !Objects.equals(this.fooPassword, ctx.currentVal());
    }
}
