package io.kings.framework.log.desensitize;

import java.util.Arrays;

/**
 * 匹配实现方式 默认关键字匹配、正则两种方案
 * <br>大日志脱敏情况下 关键字匹配性能更优 因为更少的字符串操作及缓存
 *
 * @author lun.wang
 * @date 2021/12/29 5:01 PM
 * @since v1.2
 */
public enum MatchType {
    KEYWORD, REGEXP;

    private static class NoSuchMatchType extends DesensitizeException {

        public NoSuchMatchType() {
            super("no such match type!");
        }
    }

    static MatchType of(String word) {
        return Arrays.stream(MatchType.values()).filter(i -> i.name().equalsIgnoreCase(word))
            .findFirst().orElseThrow(NoSuchMatchType::new);
    }
}
