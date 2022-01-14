package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeContext;
import io.kings.framework.log.desensitize.DesensitizeException;

/**
 * 中文姓名策略
 *
 * @author lun.wang
 * @date 2021/12/21 11:04 AM
 * @since v1.1
 */
class ChineseNameDesensitize extends AbstractLogDesensitize implements DesensitizeApi {

    @Override
    public boolean valid(DesensitizeContext ctx) {
        return ctx.currentValLen() > 0;
    }

    /**
     * 中文姓名策略
     * <br> 张三 ==> *三
     * <br> 张三丰 ==> 张*丰
     * <br> 易烊千玺 ==> 易**玺
     */
    @Override
    public String doDesensitize(SecurityLogAppender ctx) {
        final String src = ctx.currentVal();
        final int len = ctx.currentValLen();
        if (len < 2) {
            return ctx.appendStar(1).flush().after();
        }
        switch (len) {
            case 2:
                return ctx.append(0, 1).appendStar(1).flush().after();
            case 3:
                return ctx.append(0, 1).appendStar(1).append(2, 3).flush().after();
            case 4:
                return ctx.append(0, 1).appendStar(2).append(3, 4).flush().after();
            default:
                throw new DesensitizeException("Illegal Chinese name:" + src);
        }
    }
}
