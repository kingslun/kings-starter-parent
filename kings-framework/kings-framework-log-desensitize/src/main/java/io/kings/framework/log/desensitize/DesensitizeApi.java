package io.kings.framework.log.desensitize;

/**
 * 脱敏顶层接口
 *
 * @author lun.wang
 * @date 2021/12/21 10:10 AM
 * @since v1.1
 */
public interface DesensitizeApi extends IogCondition {

    /**
     * 逻辑脱敏
     *
     * @param ctx 脱敏上下文
     * @return desc脱敏结果
     */
    String desensitize(DesensitizeContext ctx);
}
