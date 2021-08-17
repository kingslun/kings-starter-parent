package io.kings.framework.election.leader;

import io.kings.framework.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * <p>
 * 基于Redis的redisson分布式锁实现的简单选举
 * </p>
 *
 * @author lun.wang
 * @date 2021/6/28 11:19 上午
 * @since v1.0
 */
@Slf4j
final class RedisDistributedElectionRegistry extends DistributedElectionRegistry.AbstractDistributedElectionRegistry
        implements DistributedElectionRegistry {
    private final DistributedElectionProperties.Redis properties;
    private final ScheduledExecutorService leaderElection;
    private final RedisTemplate<String, String> template;
    private final Selector selector;
    private static final String LOCAL_HOST = IpUtil.getIp();

    RedisDistributedElectionRegistry(RedisTemplate<String, String> template,
                                     DistributedElectionProperties.Redis properties) {
        super();
        this.template = template;
        this.properties = properties;
        leaderElection =
                Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, properties.getElectionThreadName()));
        this.selector = new Selector(this.template, this.properties, super.elections());
    }

    @Override
    protected void onRegister(DistributedElection leaderElection) {
        super.onRegister(leaderElection);
        this.selector.addElection(leaderElection);
    }

    @Override
    public void complete() {
        log.debug("RedisLeaderShip startup...");
        //register this server
        this.template.opsForZSet().add(properties.getGroups(), LOCAL_HOST, LOCAL_HOST.hashCode());
        //定时轮询 注意和scheduleWithFixedDelay的区别会在线程抛出异常时中断
        this.leaderElection.scheduleAtFixedRate(this.selector, properties.getInitialDelay(), properties.getInterval(),
                properties.getIntervalUnit());
    }

    @Override
    public void destroy() {
        log.debug("RedisLeaderShip shutdown...");
        this.leaderElection.shutdown();
        //remove this server
        this.template.opsForZSet().remove(properties.getGroups(), LOCAL_HOST);
    }

    /**
     * 定时轮询线程 轮询校验本机是否为leader
     * 校验标准参考redis master-election节点值 根据IP hashcode最小值为标准
     */
    private static class Selector implements Runnable {
        private final RedisTemplate<String, String> template;
        private final DistributedElectionProperties.Redis properties;
        private final Collection<DistributedElection> elections;

        Selector(RedisTemplate<String, String> template, DistributedElectionProperties.Redis properties,
                 Collection<DistributedElection> elections) {
            this.template = template;
            this.properties = properties;
            this.elections = CollectionUtils.isEmpty(elections) ? Collections.emptyList() : elections;
        }

        void addElection(DistributedElection election) {
            this.elections.add(election);
        }

        @Override
        public void run() {
            log.debug("Leader electing...");
            //select at having electors
            if (elections.isEmpty()) {
                return;
            }
            Set<ZSetOperations.TypedTuple<String>> masters =
                    template.opsForZSet().reverseRangeWithScores(properties.getGroups(), 0, 0);
            assert masters != null;
            Optional<ZSetOperations.TypedTuple<String>> optional = masters.stream().findFirst();
            String leader = optional.map(ZSetOperations.TypedTuple::getValue).orElse(null);
            selectCompleted(Objects.equals(leader, LOCAL_HOST));
        }

        private void selectCompleted(final boolean master) {
            if (this.first || this.master != master) {
                this.elections.forEach(elector -> {
                    if (master) {
                        elector.leader();
                    } else {
                        elector.lostLeader();
                    }
                });
                this.master = master;
                this.first = false;
            }
        }

        private boolean master = false;
        private boolean first = true;
    }
}