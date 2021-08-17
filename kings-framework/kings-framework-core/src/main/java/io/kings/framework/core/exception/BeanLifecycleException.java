package io.kings.framework.core.exception;

/**
 * 公共异常定义
 * 必须抛出或捕获本异常
 *
 * @author lun.wang
 * @date 2021/8/10 5:20 下午
 * @since v1.0
 */
public class BeanLifecycleException extends Exception {
    public BeanLifecycleException() {
        super();
    }

    public BeanLifecycleException(String message) {
        super(message);
    }

    public BeanLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanLifecycleException(Throwable cause) {
        super(cause);
    }
}
