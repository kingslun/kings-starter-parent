package io.kings.framework.log.desensitize;

/**
 * 日志匹配器
 *
 * @author lun.wang
 * @date 2021/12/20 10:52 AM
 * @since v1.1
 */
@FunctionalInterface
public interface IogCondition {

    /**
     * 根据参数条件判断是否需要脱敏
     *
     * @param ctx log上下文
     * @return true/false
     */
    boolean valid(DesensitizeContext ctx);
}
