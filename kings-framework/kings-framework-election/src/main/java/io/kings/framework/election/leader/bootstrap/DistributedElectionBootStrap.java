package io.kings.framework.election.leader.bootstrap;

import io.kings.framework.election.leader.DistributedElection;
import io.kings.framework.election.leader.DistributedElectionRegistry;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

/**
 * bootstrap处理器
 *
 * @author lun.wang
 * @date 2021/8/10 4:59 下午
 * @since v2.0
 */
@ConditionalOnBean(DistributedElectionRegistry.class)
class DistributedElectionBootStrap implements ApplicationContextAware {

    private final DistributedElectionRegistry registry;

    DistributedElectionBootStrap(DistributedElectionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DistributedElection> elections =
            applicationContext.getBeansOfType(DistributedElection.class);
        if (!CollectionUtils.isEmpty(elections)) {
            elections.values().forEach(this.registry::register);
        }
    }
}
