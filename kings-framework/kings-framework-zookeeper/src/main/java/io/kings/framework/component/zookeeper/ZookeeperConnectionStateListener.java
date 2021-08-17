package io.kings.framework.component.zookeeper;

import java.util.function.Consumer;

/**
 * 连接zk服务器的状态监听器 在断开连接或者重连等状态触发时调用对应api
 *
 * @author lun.wang
 * @date 2020/4/22 2:56 下午
 * 回调参数列表包含zookeeper本生 可以直接使用
 * @see KingsZookeeper
 * @see Consumer
 * @since v2.5.2
 */
public interface ZookeeperConnectionStateListener {
    /**
     * 连接状态 正常连接成功之后调用一次
     *
     * @param consumer 消费者
     */
    default void connected(KingsZookeeper consumer) {

    }

    /**
     * 拒绝连接状态 正常拒绝连接之后调用一次
     *
     * @param consumer 消费者
     */
    default void suspended(KingsZookeeper consumer) {

    }

    /**
     * 重连状态 正常重连成功之后调用一次
     *
     * @param consumer 消费者
     */
    default void reconnected(KingsZookeeper consumer) {

    }

    /**
     * 连接中断 正常连接被中断之后调用一次 如zk server宕机等
     *
     * @param consumer 消费者
     */
    default void lost(KingsZookeeper consumer) {

    }

    /**
     * 只读状态 当zk连接被设定为只读时调用一次
     *
     * @param consumer 消费者
     */
    default void readOnly(KingsZookeeper consumer) {

    }
}
