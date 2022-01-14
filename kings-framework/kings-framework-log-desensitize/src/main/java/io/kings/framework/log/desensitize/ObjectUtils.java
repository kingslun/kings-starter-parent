package io.kings.framework.log.desensitize;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 工具类
 *
 * @author lun.wang
 * @date 2022/1/10 2:32 PM
 * @since v1.2
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtils {

    /**
     * 判断字符串不为空
     *
     * @param str source string
     * @return true/false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean notEmpty(Object[] array) {
        return !isEmpty(array);
    }
}
