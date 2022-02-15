package io.kings.framework.devops.kubernetes;

/**
 * k8s接口 可进行namespace/pod/deployment/job等资源crud操作
 *
 * @param <C> client type
 * @author lun.wang
 * @date 2021/8/3 1:53 下午
 * @since v2.0
 */
public interface KubernetesApiFactory {

    KubernetesApi instance(String env);
}
