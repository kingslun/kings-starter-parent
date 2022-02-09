package io.kings.framework.component.zookeeper;

/**
 * zk连接状态观视者
 *
 * @author lun.wang
 * @date 2020/4/23 10:53 上午
 * @since v2.5.2
 */
interface ZookeeperConnectStateWatcher<K, V> {

    /**
     * 初始化
     *
     * @param zookeeper zk client
     * @param k         key
     * @param v         after value
     */
    default void initialized(KingsZookeeper zookeeper, K k, V v) {

    }

    /**
     * ZK挂掉一段时间后
     *
     * @param zookeeper zk client
     * @param k         key
     * @param v         after value
     */
    default void connectLost(KingsZookeeper zookeeper, K k, V v) {

    }

    /**
     * ZK挂掉
     *
     * @param zookeeper zk client
     * @param k         key
     * @param v         after value
     */
    default void connectSuspended(KingsZookeeper zookeeper, K k, V v) {

    }

    /**
     * 重新启动ZK
     *
     * @param zookeeper zk client
     * @param k         key
     * @param v         after value
     */
    default void connectReconnect(KingsZookeeper zookeeper, K k, V v) {

    }
}
