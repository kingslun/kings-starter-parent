package io.kings.framework.devops.kubernetes;

/**
 * svc资源管理
 *
 * @author lun.wang
 * @date 2022/2/16 10:04 AM
 * @since v2.3
 */
public interface ServiceResource extends KubernetesResource{

    void createService(String namespace);

    /**
     * svc状态检查
     *
     * @param name svc name
     * @return status
     */
    Object status(String namespace,String name);
}
