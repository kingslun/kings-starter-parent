package io.kings.framework.component.zookeeper;

/**
 * 节点监听器
 * 对一个节点进行监听，监听事件包括指定的路径节点的增、删、改的操作 调用的时候返回改变后的值
 *
 * @author lun.wang
 * @date 2020/4/22 7:37 下午
 * @since v2.7.2
 */
@FunctionalInterface
public interface ZookeeperPathWatcher<P, D> {
    /**
     * 节点值发生变化
     *
     * @param path  节点名称
     * @param after 变化后的值
     */
    void nodeChanged(P path, D after);
}
