package io.kings.framework.component.zookeeper.exception;

import io.kings.framework.core.exception.BeanLifecycleException;

/**
 * zookeeper异常
 *
 * @author lun.wang
 * @date 2020/4/20 5:21 下午
 * @since v2.7.5
 */
public class ZookeeperException extends BeanLifecycleException {
    public ZookeeperException() {
        super();
    }

    public ZookeeperException(String message) {
        super(message);
    }

    public ZookeeperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperException(Throwable cause) {
        super(cause);
    }
}
