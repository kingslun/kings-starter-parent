package io.kings.framework.log.desensitize.match;

/**
 * 脱敏字符串段
 *
 * @author lun.wang
 * @date 2021/12/28 4:19 PM
 * @since v1.2
 */
public interface Segment {

    /**
     * 脱敏字符串在源日志字符串中的开始下标
     *
     * @return number
     */
    int beginIndex();

    /**
     * 脱敏字符串在源日志字符串中的截止下标
     *
     * @return number
     */
    int endIndex();

    /**
     * 截取脱敏字符时 对源字符串所有字符逐一解析
     * <br>判断当前字符是否需要加入到脱敏字符中
     *
     * @param ch char
     * @return true/false
     */
    default boolean skipped(char ch) {
        return true;
    }
}
