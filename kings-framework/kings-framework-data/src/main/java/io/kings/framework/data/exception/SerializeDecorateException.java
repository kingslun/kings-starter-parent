package io.kings.framework.data.exception;

/**
 * 序列装饰器异常
 *
 * @author lun.wang
 * @date 2021/8/16 10:44 上午
 * @since v2.0
 */
public class SerializeDecorateException extends SerializeException {

    public SerializeDecorateException() {
        super();
    }

    public SerializeDecorateException(String message) {
        super(message);
    }

    public SerializeDecorateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeDecorateException(Throwable cause) {
        super(cause);
    }
}
