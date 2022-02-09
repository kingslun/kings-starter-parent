package io.kings.framework.election.leader;

import io.kings.framework.core.Nameable;

/**
 * 分布式选举方案 可以采用多种实现 目前采用zookeeper、Redis
 *
 * @author lun.wang
 * @date 2020/4/20 4:31 下午
 * @since v2.0
 */
public interface DistributedElection extends Nameable {

    /**
     * is leader 本机为leader时调用 处理相关逻辑
     */
    default void leader() {

    }

    /**
     * lost leader 本机丢失leader时调用 处理相关逻辑
     */
    default void lostLeader() {

    }
}
