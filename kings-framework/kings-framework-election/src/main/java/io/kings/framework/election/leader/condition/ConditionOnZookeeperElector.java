package io.kings.framework.election.leader.condition;

import io.kings.framework.core.condition.PropertyCondition;
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
@Conditional(ConditionOnZookeeperElector.OnZookeeperElectorCondition.class)
@ConditionOnElection
public @interface ConditionOnZookeeperElector {
    class OnZookeeperElectorCondition extends PropertyCondition {
        @Override
        protected boolean matches() {
            return super.expectedly(ELECTION_TYPE_PREFIX, "zookeeper") ||
                    super.expectedly(ELECTION_TYPE_PREFIX, "ZOOKEEPER");
        }

        @Override
        public String onMatch() {
            return "ZookeeperElector Condition Success";
        }

        @Override
        protected String onMismatch() {
            return String.format(
                    "ZookeeperElector Condition Failure,Maybe because the configuration:%s is missing or not zookeeper",
                    ELECTION_TYPE_PREFIX);
        }
    }
}
