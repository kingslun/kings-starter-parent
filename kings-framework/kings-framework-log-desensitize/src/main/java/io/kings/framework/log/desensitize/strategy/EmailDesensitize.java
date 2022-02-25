package io.kings.framework.log.desensitize.strategy;

import static io.kings.framework.log.desensitize.StringAppenderFacade.CHAR_AT;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeContext;

/**
 * 邮箱策略
 *
 * @author lun.wang
 * @date 2021/12/21 11:04 AM
 * @since v1.1
 */
class EmailDesensitize extends AbstractLogDesensitize implements DesensitizeApi {

    @Override
    public boolean valid(DesensitizeContext ctx) {
        return ctx.currentVal().indexOf(CHAR_AT) > 1;
    }

    /**
     * 邮箱策略格式为前三个字符到@符号间会被脱敏<font>可能需要注意的是@前本身只有三个字符的脱敏会毫无效果</font>
     */
    @Override
    public String doDesensitize(SecurityLogAppender ctx) {
        final String src = ctx.currentVal();
        final int len = ctx.currentValLen();
        if (len < 3) {
            return src;
        }
        final int at = src.indexOf(CHAR_AT);
        return ctx.append(0, 3).appendStar(at - 3).append(at, len).flush().after();
    }
}
