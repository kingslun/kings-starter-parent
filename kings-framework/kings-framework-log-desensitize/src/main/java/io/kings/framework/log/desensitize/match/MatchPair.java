package io.kings.framework.log.desensitize.match;

import io.kings.framework.log.desensitize.strategy.Strategy;

/**
 * 匹配结果描述对象
 *
 * @author lun.wang
 * @date 2021/12/29 2:36 PM
 * @since v1.2
 */
public interface MatchPair extends Segment {

    Strategy strategy();
}
