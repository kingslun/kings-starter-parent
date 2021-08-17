package io.kings.framework.devops.kubernetes;

import io.kings.framework.devops.kubernetes.exception.KubernetesNetworkException;

/**
 * k8s网络管理
 * 可以创建service/ingress等资源crud操作
 *
 * @author lun.wang
 * @date 2021/8/3 2:08 下午
 * @since v2.0
 */
public interface NetworkResource {
    void createService() throws KubernetesNetworkException;

    void createIngress() throws KubernetesNetworkException;
}
