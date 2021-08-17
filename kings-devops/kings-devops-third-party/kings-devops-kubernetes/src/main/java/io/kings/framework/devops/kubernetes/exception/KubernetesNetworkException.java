package io.kings.framework.devops.kubernetes.exception;

/**
 * k8s接口错误
 *
 * @author lun.wang
 * @date 2021/8/3 3:35 下午
 * @since v2.0
 */
public class KubernetesNetworkException extends KubernetesException {
    public KubernetesNetworkException(Throwable cause) {
        super(cause);
    }

    public KubernetesNetworkException(String message) {
        super(message);
    }
}
