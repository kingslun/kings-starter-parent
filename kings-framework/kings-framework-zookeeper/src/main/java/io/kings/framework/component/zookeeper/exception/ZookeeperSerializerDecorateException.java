package io.kings.framework.component.zookeeper.exception;

/**
 * zookeeper异常
 * <p>序列化错误描述对象</p>
 *
 * @author lun.wang
 * @date 2020/07/06 16:00
 * @since v2.8.6
 */
public class ZookeeperSerializerDecorateException extends ZookeeperSerializeException {
    public ZookeeperSerializerDecorateException() {
        super();
    }

    public ZookeeperSerializerDecorateException(String message) {
        super(message);
    }

    public ZookeeperSerializerDecorateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperSerializerDecorateException(Throwable cause) {
        super(cause);
    }
}
