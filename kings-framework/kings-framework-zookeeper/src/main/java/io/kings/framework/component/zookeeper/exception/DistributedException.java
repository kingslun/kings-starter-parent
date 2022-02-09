package io.kings.framework.component.zookeeper.exception;

import io.kings.framework.core.exception.BeanLifecycleException;

/**
 * 分布式一致性异常超类
 *
 * @author lun.wang
 * @date 2020/4/20 5:22 下午
 * @since v2.7.5
 */
public class DistributedException extends BeanLifecycleException {

    public DistributedException() {
        super();
    }

    public DistributedException(String message) {
        super(message);
    }

    public DistributedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributedException(Throwable cause) {
        super(cause);
    }
}
