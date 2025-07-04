package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.DistributedElectionException;
import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import io.kings.framework.election.leader.DistributedElection;
import io.kings.framework.election.leader.DistributedElectionProperties;
import io.kings.framework.election.leader.DistributedElectionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * 默认zookeeper-leader选举实现
 *
 * @author lun.wang
 * @date 2020/4/22 11:22 上午
 * @since v2.7.2
 */
@Slf4j
class ZookeeperDistributedElectionRegistry extends
        DistributedElectionRegistry.AbstractDistributedElectionRegistry
        implements Zookeeper, DistributedElectionRegistry {

    /**
     * 内部线程池获取 可以暴露出去给外部使用
     *
     * @return Executor
     * @throws ZookeeperException failed null pointer
     * @see ExecutorService
     * @see Executor
     */
    @Override
    public ExecutorService threadPool() throws ZookeeperException {
        throw new ZookeeperException("Not support operation");
    }

    @Override
    protected void onRegister(DistributedElection leaderElection) {
        super.onRegister(leaderElection);
        this.leaderLatch.addListener(new DistributedElectionFacade(leaderElection));
    }

    /**
     * init method
     *
     * @throws ZookeeperException open failed
     */
    @Override
    public void complete() throws ZookeeperException {
        try {
            this.leaderLatch.start();
            //add listener
            super.elections()
                    .forEach(e -> this.leaderLatch.addListener(new DistributedElectionFacade(e)));
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    private final LeaderLatch leaderLatch;

    ZookeeperDistributedElectionRegistry(CuratorFramework client,
                                         DistributedElectionProperties.Zookeeper zookeeper)
            throws DistributedElectionException {
        try {
            Assert.notNull(client, "zookeeper client is null");
            Assert.hasText(zookeeper.getPath(), "election path for zookeeper client is empty");
            //leader master
            this.leaderLatch = new LeaderLatch(client, path0(zookeeper.getPath()));
        } catch (Exception e) {
            throw new DistributedElectionException(e);
        }
    }

    /**
     * leader listener for zookeeper election Facade模式
     */
    private static class DistributedElectionFacade implements LeaderLatchListener {

        @Override
        public void isLeader() {
            delegated.leader();
        }

        @Override
        public void notLeader() {
            delegated.lostLeader();
        }

        private final DistributedElection delegated;

        DistributedElectionFacade(DistributedElection distributedElection) {
            Assert.notNull(distributedElection, "DistributedElection is null");
            this.delegated = distributedElection;
        }
    }

    /**
     * close method
     *
     * @throws ZookeeperException failed to close
     */
    @Override
    public void close() throws ZookeeperException {
        try {
            leaderLatch.close();
        } catch (IOException e) {
            throw new ZookeeperException(e);
        }
    }
}
