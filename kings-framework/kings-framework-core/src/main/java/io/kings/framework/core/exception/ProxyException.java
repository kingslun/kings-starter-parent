package io.kings.framework.core.exception;

/**
 * 非受检异常 代理类异常
 *
 * @author lun.wang
 * @date 2021/8/10 5:20 下午
 * @since v1.0
 */
public class ProxyException extends RuntimeException {

    public ProxyException() {
        super();
    }

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }
}
