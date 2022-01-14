package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeContext;
import io.kings.framework.log.desensitize.StringAppenderFacade.StringAppender;

/**
 * 安全的字符拼接
 *
 * @author lun.wang
 * @date 2022/1/10 2:11 PM
 * @since v1.2
 */
class SecurityLogAppender extends StringAppender implements DesensitizeContext {

    private SecurityLogAppender(String security) {
        super(security);
    }

    SecurityLogAppender(DesensitizeContext ctx) {
        this(ctx.currentVal());
    }

    @Override
    public String currentVal() {
        return super.source();
    }
}
