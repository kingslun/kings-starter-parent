package io.kings.framework.component.zookeeper.exception;

/**
 * zookeeper异常
 *
 * @author lun.wang
 * @date 2020/4/20 5:21 下午
 * @since v2.7.5
 */
public class ZookeeperWatcherException extends ZookeeperException {

    public ZookeeperWatcherException() {
        super();
    }

    public ZookeeperWatcherException(String message) {
        super(message);
    }

    public ZookeeperWatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperWatcherException(Throwable cause) {
        super(cause);
    }
}
