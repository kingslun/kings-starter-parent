package io.kings.framework.election.leader;

import lombok.extern.slf4j.Slf4j;

/**
 * 分布式选举抽象类
 *
 * @author lun.wang
 * @date 2021/8/10 3:35 下午
 * @since v2.0
 */
@Slf4j
public abstract class AbstractDistributedElection implements DistributedElection {
    /**
     * is leader
     * 本机为leader时调用 处理相关逻辑
     */
    @Override
    public void leader() {
        if (log.isDebugEnabled()) {
            log.debug("=====>>>this machine is leader");
        }
    }

    /**
     * lost leader
     * 本机丢失leader时调用 处理相关逻辑
     */
    @Override
    public void lostLeader() {
        if (log.isDebugEnabled()) {
            log.debug("=====>>>this machine is no more leader");
        }
    }
}
