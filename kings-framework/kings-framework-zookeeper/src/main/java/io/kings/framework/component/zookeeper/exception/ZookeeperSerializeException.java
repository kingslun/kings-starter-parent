package io.kings.framework.component.zookeeper.exception;

/**
 * zookeeper异常
 *
 * @author lun.wang
 * @date 2020/07/06 16:00
 * @since v2.8.6
 */
public class ZookeeperSerializeException extends DistributedException {
    public ZookeeperSerializeException() {
        super();
    }

    public ZookeeperSerializeException(String message) {
        super(message);
    }

    public ZookeeperSerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperSerializeException(Throwable cause) {
        super(cause);
    }
}
