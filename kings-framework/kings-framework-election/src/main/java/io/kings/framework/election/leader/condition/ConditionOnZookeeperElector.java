package io.kings.framework.election.leader.condition;

import static io.kings.framework.election.leader.DistributedElectionProperties.ELECTION_TYPE_PREFIX;

import io.kings.framework.core.condition.AbstractPropertyCondition;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

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
@Conditional(ConditionOnZookeeperElector.OnZookeeperElectorCondition.class)
@ConditionOnElection
public @interface ConditionOnZookeeperElector {

    class OnZookeeperElectorCondition extends AbstractPropertyCondition {

        @Override
        public boolean match() {
            return super.expectation(ELECTION_TYPE_PREFIX, "zookeeper") ||
                super.expectation(ELECTION_TYPE_PREFIX, "ZOOKEEPER");
        }

        @Override
        public String onMatched() {
            return "ZookeeperElector Condition Success";
        }

        @Override
        public String onMismatch() {
            return String.format(
                "ZookeeperElector Condition Failure,Maybe because the configuration:%s is missing or not zookeeper",
                ELECTION_TYPE_PREFIX);
        }
    }
}
