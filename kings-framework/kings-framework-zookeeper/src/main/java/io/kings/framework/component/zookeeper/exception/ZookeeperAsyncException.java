package io.kings.framework.component.zookeeper.exception;

/**
 * <zookeeper异步异常
 *
 * @author lun.wang
 * @date 2020/4/20 5:21 下午
 * @since v2.7.5
 */
public class ZookeeperAsyncException extends ZookeeperException {
    public ZookeeperAsyncException() {
        super();
    }

    public ZookeeperAsyncException(String message) {
        super(message);
    }

    public ZookeeperAsyncException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperAsyncException(Throwable cause) {
        super(cause);
    }
}
