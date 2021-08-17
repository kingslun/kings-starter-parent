package io.kings.framework.component.zookeeper;

import io.kings.framework.data.serializer.Serializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.springframework.util.Assert;

import java.util.concurrent.ExecutorService;

/**
 * zk客户端状态机
 *
 * @author lun.wang
 * @date 2020/4/22 3:24 下午
 * @since v2.5.2
 */
class ZookeeperConnectionStateMonitor implements ConnectionStateListener {
    /**
     * thread pool
     */
    private final ExecutorService threadPool;
    /**
     * connection status listener
     */
    private final ZookeeperConnectionStateListener connectionStateListener;
    /**
     * zk value序列化器组件
     */
    private final Serializer serializer;

    ZookeeperConnectionStateMonitor(
            ExecutorService threadPool,
            ZookeeperConnectionStateListener connectionStateListener,
            Serializer serializer) {
        Assert.notNull(threadPool, "thread pool is null");
        Assert.notNull(connectionStateListener, "connection state listener is null");
        Assert.notNull(serializer, "[ZookeeperSerializer] is null");
        this.threadPool = threadPool;
        this.serializer = serializer;
        this.connectionStateListener = connectionStateListener;
    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        switch (connectionState) {
            case RECONNECTED:
                connectionStateListener
                        .reconnected(new ZookeeperProvider(curatorFramework, this.threadPool, this.serializer));
                break;
            case LOST:
                connectionStateListener
                        .lost(new ZookeeperProvider(curatorFramework, this.threadPool, this.serializer));
                break;
            case CONNECTED:
                connectionStateListener.connected(
                        new ZookeeperProvider(curatorFramework, this.threadPool, this.serializer));
                break;
            case READ_ONLY:
                connectionStateListener
                        .readOnly(new ZookeeperProvider(curatorFramework, this.threadPool, this.serializer));
                break;
            case SUSPENDED:
                connectionStateListener.suspended(
                        new ZookeeperProvider(curatorFramework, this.threadPool, this.serializer));
                break;
            default:
                break;
        }
    }
}
