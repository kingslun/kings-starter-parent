package io.kings.framework.devops.kubernetes;

import io.kings.framework.core.bean.BeanLifecycle;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import java.util.function.Supplier;

/**
 * k8s接口 可进行namespace/pod/deployment/job等资源crud操作
 *
 * @param <C> client type
 * @author lun.wang
 * @date 2021/8/3 1:53 下午
 * @since v2.0
 */
public interface KubernetesApi<C> extends BeanLifecycle {

    /**
     * 获取操作pod资源的API
     *
     * @param id             兼容旧版本 n_kubernetes表的ID
     * @param clientSupplier 用于缓存未取出对应K8s信息时读取
     * @return PodResource
     */
    PodResource podResource(Long id, Supplier<C> clientSupplier);

    /**
     * 获取操作deployment资源的API
     *
     * @param id             兼容旧版本 n_kubernetes表的ID
     * @param clientSupplier 用于缓存未取出对应K8s信息时读取
     * @return PodResource
     */
    DeploymentResource deploymentResource(Long id, Supplier<C> clientSupplier);

    /**
     * @param id             k8sID
     * @param clientSupplier 提供客户端
     * @return NetworkResource
     */
    NetworkResource networkResource(Long id, Supplier<C> clientSupplier);

    /**
     * 销毁任务
     *
     * @throws KubernetesException destroy failure
     */
    @Override
    void destroy() throws KubernetesException;

    /**
     * 启动任务
     *
     * @throws KubernetesException start failure
     */
    @Override
    void complete() throws KubernetesException;
}
