package io.kings.framework.election.leader.condition;

import io.kings.framework.core.condition.AbstractPropertyCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

import static io.kings.framework.election.leader.DistributedElectionProperties.ELECTION_SWITCH_PREFIX;

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
@Conditional(ConditionOnElection.OnElectionCondition.class)
@interface ConditionOnElection {

    class OnElectionCondition extends AbstractPropertyCondition {

        @Override
        public boolean match() {
            return super.on(ELECTION_SWITCH_PREFIX, true);
        }

        @Override
        public String onMatched() {
            return "Election Condition Success";
        }

        @Override
        public String onMismatch() {
            return String.format(
                    "Election Condition Failure,Maybe because the configuration:%s is missing or off/false",
                    ELECTION_SWITCH_PREFIX);
        }
    }
}
