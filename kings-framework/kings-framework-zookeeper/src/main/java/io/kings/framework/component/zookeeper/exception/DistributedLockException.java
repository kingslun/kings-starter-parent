package io.kings.framework.component.zookeeper.exception;

/**
 * 分布式锁操作异常
 *
 * @author lun.wang
 * @date 2020/4/20 4:03 下午
 * @since v2.7.5
 */
public class DistributedLockException extends RuntimeException {

    public DistributedLockException(String message) {
        super(message);
    }

    public DistributedLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributedLockException(Throwable cause) {
        super(cause);
    }
}
