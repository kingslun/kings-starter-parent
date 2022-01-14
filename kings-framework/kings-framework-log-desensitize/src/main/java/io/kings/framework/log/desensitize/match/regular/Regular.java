package io.kings.framework.log.desensitize.match.regular;

import io.kings.framework.log.desensitize.strategy.Strategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关键字规则
 * <br>Extension point
 *
 * @author lun.wang
 * @date 2022/1/6 2:15 PM
 * @since v1.2
 */
@Getter
@AllArgsConstructor
public class Regular {

    private Strategy strategy;
    private final String[] alias;
    private final char[] endWith;
}
