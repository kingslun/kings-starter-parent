package io.kings.framework.component.zookeeper;

/**
 * 异步操作回调函数
 *
 * @author lun.wang
 * @date 2020/4/24 3:28 下午
 * @since v2.7.2
 */
@FunctionalInterface
public interface ZookeeperAsyncCallback<K, V> {

    /**
     * callback
     *
     * @param operator operator
     * @param response response
     */
    void call(ZookeeperAsync<K, V> operator, ZookeeperAsyncResponse response);
}
