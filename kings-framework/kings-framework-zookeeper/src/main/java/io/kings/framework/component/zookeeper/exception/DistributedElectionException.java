package io.kings.framework.component.zookeeper.exception;

/**
 * 分布式选举异常
 *
 * @author lun.wang
 * @date 2020/4/20 4:38 下午
 * @since v2.7.5
 */
public class DistributedElectionException extends DistributedException {
    public DistributedElectionException() {
        super();
    }

    public DistributedElectionException(String message) {
        super(message);
    }

    public DistributedElectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributedElectionException(Throwable cause) {
        super(cause);
    }
}
