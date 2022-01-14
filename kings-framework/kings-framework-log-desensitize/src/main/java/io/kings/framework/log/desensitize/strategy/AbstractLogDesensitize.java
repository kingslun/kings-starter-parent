package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeContext;

/**
 * log脱敏条件抽象类
 *
 * @author lun.wang
 * @date 2022/1/10 11:34 AM
 * @since v1.2
 */
abstract class AbstractLogDesensitize implements DesensitizeApi {

    @Override
    public final String desensitize(DesensitizeContext ctx) {
        if (this.valid(ctx)) {
            return this.doDesensitize(new SecurityLogAppender(ctx));
        }
        //if not valid return source
        return ctx.currentVal();
    }

    protected abstract String doDesensitize(SecurityLogAppender ctx);
}
