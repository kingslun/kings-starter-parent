package io.kings.framework.log.desensitize.match.regular;

import io.kings.framework.log.desensitize.DesensitizeException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

/**
 * logback脱敏规则
 *
 * @author lun.wang
 * @date 2022/1/6 2:43 PM
 * @since v1.2
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegularHolder {

    private static Map<String, Regular> regulars;

    public static void cacheRegular(Map<String, Regular> regulars) {
        if (regulars == null) {
            throw new DesensitizeException("Illegal arguments");
        }
        //un support modify
        RegularHolder.regulars = Collections.unmodifiableMap(regulars);
    }

    public static Map<String, Regular> regulars() {
        return RegularHolder.regulars;
    }
}
