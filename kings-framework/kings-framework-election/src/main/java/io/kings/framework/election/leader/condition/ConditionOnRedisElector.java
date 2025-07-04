package io.kings.framework.election.leader.condition;

import io.kings.framework.core.condition.AbstractPropertyCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

import static io.kings.framework.election.leader.DistributedElectionProperties.ELECTION_TYPE_PREFIX;

/**
 * redis选举器
 *
 * @author lun.wang
 * @date 2021/8/10 7:15 下午
 * @since v2.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionOnRedisElector.OnRedisElectorCondition.class)
@ConditionOnElection
public @interface ConditionOnRedisElector {

    class OnRedisElectorCondition extends AbstractPropertyCondition {

        @Override
        public boolean match() {
            return super.expectation(ELECTION_TYPE_PREFIX, "redis") || super.expectation(
                    ELECTION_TYPE_PREFIX, "REDIS");
        }

        @Override
        public String onMatched() {
            return "RedisElector Condition Success";
        }

        @Override
        public String onMismatch() {
            return String.format(
                    "RedisElector Condition Failure,Maybe because the configuration:%s is missing or not redis",
                    ELECTION_TYPE_PREFIX);
        }
    }
}
