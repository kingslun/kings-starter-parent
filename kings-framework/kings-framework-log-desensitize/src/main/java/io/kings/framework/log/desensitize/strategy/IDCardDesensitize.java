package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeContext;

/**
 * 身份证策略
 *
 * @author lun.wang
 * @date 2021/12/21 11:04 AM
 * @since v1.1
 */
class IDCardDesensitize extends AbstractLogDesensitize implements DesensitizeApi {

    @Override
    public boolean valid(DesensitizeContext ctx) {
        return ctx.currentValLen() == 18;
    }

    /**
     * 身份证策略
     * <br>421023197709221069 ==> 4210**********1069
     */
    @Override
    public String doDesensitize(SecurityLogAppender ctx) {
        return ctx.append(0, 4).appendStar(10).append(14, ctx.currentValLen()).flush().after();
    }
}
