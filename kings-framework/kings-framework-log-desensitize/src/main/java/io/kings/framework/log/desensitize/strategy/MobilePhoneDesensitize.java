package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeContext;

/**
 * 手机号策略
 *
 * @author lun.wang
 * @date 2021/12/21 11:04 AM
 * @since v1.1
 */
class MobilePhoneDesensitize extends AbstractLogDesensitize implements DesensitizeApi {

    @Override
    public boolean valid(DesensitizeContext ctx) {
        return ctx.currentValLen() == 11;
    }

    /**
     * 手机号策略
     * <br>15021261772 ==> 150****1772
     */
    @Override
    public String doDesensitize(SecurityLogAppender ctx) {
        return ctx.append(0, 3).appendStar(4).append(7, 11).flush().after();
    }
}
