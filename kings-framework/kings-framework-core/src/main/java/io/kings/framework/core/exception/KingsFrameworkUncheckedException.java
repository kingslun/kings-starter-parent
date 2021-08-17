package io.kings.framework.core.exception;

/**
 * 公共异常定义
 * 非受检异常
 *
 * @author lun.wang
 * @date 2021/8/10 5:20 下午
 * @since v1.0
 */
public class KingsFrameworkUncheckedException extends RuntimeException {
    public KingsFrameworkUncheckedException() {
        super();
    }

    public KingsFrameworkUncheckedException(String message) {
        super(message);
    }

    public KingsFrameworkUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public KingsFrameworkUncheckedException(Throwable cause) {
        super(cause);
    }
}
