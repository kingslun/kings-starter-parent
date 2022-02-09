package io.kings.framework.component.zookeeper;

/**
 * zookeeper指定节点的子节点监听器
 *
 * @author lun.wang
 * @date 2020/4/22 8:21 下午
 * @since v2.7.2
 */
public interface ZookeeperPathChildrenWatcher<K, V> extends ZookeeperConnectStateWatcher<K, V> {

    /**
     * 添加了子节点
     *
     * @param p        path
     * @param operator zk client
     * @param d        after data
     */
    default void childAdd(KingsZookeeper operator, K p, V d) {

    }

    /**
     * 删除了子节点
     *
     * @param p        path
     * @param operator zk client
     * @param d        after data
     */
    default void childRemove(KingsZookeeper operator, K p, V d) {

    }

    /**
     * 子节点发生变化
     *
     * @param p        path
     * @param operator zk client
     * @param d        after data
     */
    default void childUpdate(KingsZookeeper operator, K p, V d) {

    }
}
