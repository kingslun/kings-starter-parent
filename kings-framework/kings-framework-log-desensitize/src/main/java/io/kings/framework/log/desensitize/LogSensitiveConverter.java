package io.kings.framework.log.desensitize;

import io.kings.framework.log.desensitize.LogContext.SensitiveLogContext;

/**
 * 日志脱敏处理
 *
 * @author lun.wang
 * @date 2021/12/8 4:08 PM
 * @since v2.1
 */
public class LogSensitiveConverter extends AbstractLogConverter {

    @Override
    String sensitive(String source) {
        try {
            LogContext context = new SensitiveLogContext(source, super.depth, super.useIntern);
            return this.logMatcher.match(context).result();
        } catch (Exception e) {
            //Print errors to the console, do not allow blocking tasks
            e.printStackTrace();
            return source;
        }
    }
}
