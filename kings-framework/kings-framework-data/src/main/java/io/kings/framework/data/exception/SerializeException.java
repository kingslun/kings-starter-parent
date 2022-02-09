package io.kings.framework.data.exception;

/**
 * 序列异常
 *
 * @author lun.wang
 * @date 2021/8/16 10:44 上午
 * @since v2.0
 */
public class SerializeException extends Exception {

    public SerializeException() {
        super();
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
