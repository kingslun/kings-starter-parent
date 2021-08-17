package io.kings.framework.core.exception;

/**
 * 公共异常定义
 * 必须抛出或捕获本异常
 *
 * @author lun.wang
 * @date 2021/8/10 5:20 下午
 * @since v1.0
 */
public class KingsFrameworkException extends Exception {
    public KingsFrameworkException() {
        super();
    }

    public KingsFrameworkException(String message) {
        super(message);
    }

    public KingsFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public KingsFrameworkException(Throwable cause) {
        super(cause);
    }
}
