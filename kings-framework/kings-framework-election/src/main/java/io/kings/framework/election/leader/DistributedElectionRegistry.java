package io.kings.framework.election.leader;

import io.kings.framework.core.bean.BeanLifecycle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface DistributedElectionRegistry extends BeanLifecycle {
    @Slf4j
    abstract class AbstractDistributedElectionRegistry implements DistributedElectionRegistry {
        private final Map<String, DistributedElection> elections;

        protected AbstractDistributedElectionRegistry() {
            this.elections = new HashMap<>(16);
        }

        /**
         * 子类实现注册入口 会在注册选择器时触发
         *
         * @param leaderElection leader选举实现类
         * @see DistributedElection
         */
        protected void onRegister(DistributedElection leaderElection) {
            log.debug("doing something after DistributedElection:{} registered", leaderElection.name());
        }

        @Override
        public final void register(@NonNull DistributedElection leaderElection) {
            if (elections.containsKey(leaderElection.name())) {
                return;
            }
            elections.put(leaderElection.name(), leaderElection);
            if (log.isDebugEnabled()) {
                log.debug("Registered named ['{}'] DistributedElection success...", leaderElection.name());
            }
            this.onRegister(leaderElection);
        }

        protected Collection<DistributedElection> elections() {
            return this.elections.values();
        }
    }

    /**
     * 分布式选举注册器
     *
     * @param election 选择器
     * @see DistributedElection
     */
    void register(@NonNull DistributedElection election);
}
