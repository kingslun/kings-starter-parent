package io.kings.framework.component.zookeeper.exception;

/**
 * zookeeper事物异常
 *
 * @author lun.wang
 * @date 2020/4/20 5:21 下午
 * @since v2.7.5
 */
public class ZookeeperTransactionException extends ZookeeperException {

    public ZookeeperTransactionException() {
        super();
    }

    public ZookeeperTransactionException(String message) {
        super(message);
    }

    public ZookeeperTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperTransactionException(Throwable cause) {
        super(cause);
    }
}
