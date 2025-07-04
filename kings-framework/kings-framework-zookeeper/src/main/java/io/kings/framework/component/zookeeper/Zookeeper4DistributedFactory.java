package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.DistributedElectionException;
import io.kings.framework.component.zookeeper.exception.DistributedException;
import io.kings.framework.core.proxy.LogProxyFacade;
import io.kings.framework.data.serializer.Serializer;
import io.kings.framework.election.leader.DistributedElectionProperties;
import io.kings.framework.election.leader.DistributedElectionRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionStateListener;

import java.util.concurrent.ExecutorService;

/**
 * the zookeeper factory
 *
 * @author lun.wang
 * @date 2020/4/22 11:56 上午
 * @since v2.7.3
 */
public class Zookeeper4DistributedFactory {

    private Zookeeper4DistributedFactory() throws DistributedException {
        throw new DistributedException("Not support operation");
    }

    /**
     * 创建zookeeper操作对象
     *
     * @param curatorFramework zk client, not null
     * @param threadPool       thread pool not null
     * @param serializer       value序列化器 @since v2.8.6
     * @return KingsZookeeper
     * @see ZookeeperProvider
     */
    public static KingsZookeeper octopusZookeeper(
            CuratorFramework curatorFramework, ExecutorService threadPool, Serializer serializer) {
        return LogProxyFacade.proxy(
                new ZookeeperProvider(curatorFramework, threadPool, serializer));
    }

    /**
     * 创建zookeeper分布式选举对象
     *
     * @param client    zk client, not null
     * @param zookeeper zk 选举配置
     * @return elector
     * @throws DistributedElectionException failed to elect
     */
    public static DistributedElectionRegistry createZookeeper4DistributedElector(
            CuratorFramework client, DistributedElectionProperties.Zookeeper zookeeper)
            throws DistributedElectionException {
        return new ZookeeperDistributedElectionRegistry(client, zookeeper);
    }

    /**
     * connection state monitor
     *
     * @param threadPool              thread pool
     * @param connectionStateListener state listener
     * @param serializer              value序列化器 @since v2.8.6
     * @return ConnectionStateListener
     */
    public static ConnectionStateListener
    connectionStateListener(ExecutorService threadPool,
                            ZookeeperConnectionStateListener connectionStateListener,
                            Serializer serializer) {
        return new ZookeeperConnectionStateMonitor(threadPool, connectionStateListener, serializer);
    }
}
