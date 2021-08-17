package io.kings.framework.devops.kubernetes.exception;

/**
 * k8s接口错误
 *
 * @author lun.wang
 * @date 2021/8/3 3:35 下午
 * @since v2.0
 */
public class KubernetesException extends RuntimeException {
    public KubernetesException(Throwable cause) {
        super(cause);
    }

    public KubernetesException(String message) {
        super(message);
    }

    public KubernetesException(String message, Throwable cause) {
        super(message, cause);
    }
}
