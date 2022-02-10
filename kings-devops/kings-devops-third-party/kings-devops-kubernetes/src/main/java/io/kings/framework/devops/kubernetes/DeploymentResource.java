package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.model.enums.DeployStatus;
import java.util.List;

/**
 * deployment资源api
 *
 * @author lun.wang
 * @date 2021/8/3 4:12 下午
 * @since v2.0
 */
public interface DeploymentResource extends KubernetesResource<DeploymentResource> {

    /**
     * 删除deployment
     *
     * @param name 名称
     * @return true/false
     */
    boolean delete(String name) throws KubernetesException;

    /**
     * 扩缩容
     *
     * @param name     名称
     * @param replicas 实例数量
     * @return true/false
     */
    boolean scale(String name, int replicas) throws KubernetesException;

    /**
     * 重启部署集
     *
     * @param name 名称
     * @return true/false
     */
    boolean restart(String name) throws KubernetesException;

    /**
     * 回滚部署
     *
     * @param name  名称
     * @param image 回滚镜像 image:tag eg=>redis:latest
     * @return true/false
     */
    boolean rollback(String name, String image) throws KubernetesException;

    /**
     * 获取deployment k8s配置
     *
     * @param name 名称
     * @return config
     */
    String getConfigYaml(String name) throws KubernetesException;

    /**
     * 根据标签或查询所有的deployment集合 kubectl get deployment |grep name labelKey和labelValue为一个过滤键值对
     * 要么同时有值要么同时为空
     *
     * @param labelKey   标签键
     * @param labelValue 标签值
     * @return Deployments
     */
    List<Deployment> getList(String labelKey, String labelValue) throws KubernetesException;

    /**
     * 精准查询deployment kubectl get deployment name
     *
     * @param name name
     * @return Deployment
     */
    Deployment getOne(String name) throws KubernetesException;

    /**
     * 重新部署 deployment kubectl apply -f deployment.yaml
     *
     * @param name name
     * @param yaml deployment配置
     * @return true/false
     */
    boolean replace(String name, String yaml) throws KubernetesException;

    /**
     * 获取部署状态
     *
     * @param name 名称
     * @return 1:进行中 3:失败 4:成功
     */
    DeployStatus getStatus(String name) throws KubernetesException;
}
