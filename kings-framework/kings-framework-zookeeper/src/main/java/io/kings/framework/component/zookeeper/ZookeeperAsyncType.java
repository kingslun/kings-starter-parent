package io.kings.framework.component.zookeeper;

/**
 * 异步事件 remove some type
 *
 * @author lun.wang
 * @date 2020/4/24 5:23 下午
 * @see org.apache.curator.framework.api.CuratorEventType
 * @since v2.7.2
 */
enum ZookeeperAsyncType {
    /**
     * 创建操作
     */
    CREATE,
    /**
     * 删除操作
     */
    DELETE,
    /**
     * 节点判断操作
     */
    NONEXISTENT,
    /**
     * 获取操作
     */
    GET,
    /**
     * 更新操作
     */
    UPDATE,
    /**
     * 获取子节点操作
     */
    CHILDREN,
    /**
     * 异步操作
     */
    SYNC,
    /**
     * 观测操作
     */
    WATCHED,
    /**
     * 关闭操作
     */
    CLOSING
}
