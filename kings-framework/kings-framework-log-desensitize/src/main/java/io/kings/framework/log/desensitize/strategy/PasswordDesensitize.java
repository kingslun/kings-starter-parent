package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeContext;

/**
 * 密码策略
 *
 * @author lun.wang
 * @date 2021/12/21 11:04 AM
 * @since v1.1
 */
class PasswordDesensitize extends AbstractLogDesensitize implements DesensitizeApi {

    @Override
    public boolean valid(DesensitizeContext ctx) {
        return !ctx.currentValEmpty();
    }

    /**
     * 密码策略
     * <br>password ==> ********
     */
    @Override
    public String doDesensitize(SecurityLogAppender ctx) {
        return ctx.appendStar(ctx.currentValLen()).flush().after();
    }
}
