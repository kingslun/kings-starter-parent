package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.kings.framework.devops.kubernetes.model.enums.DeployStatus;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * deployment资源api
 *
 * @author lun.wang
 * @date 2021/8/3 4:12 下午
 * @since v2.0
 */
public interface DeploymentResource extends KubernetesResource {

    /**
     * 删除deployment
     *
     * @param params 参数
     * @return true/false
     */
    boolean delete(Params params);

    /**
     * 扩缩容
     *
     * @param params 参数
     * @return true/false
     */
    boolean scale(Params params);

    /**
     * 重启部署集
     *
     * @param params 参数
     * @return true/false
     */
    boolean restart(Params params);

    /**
     * 回滚部署
     *
     * @param params 参数
     * @return true/false
     */
    boolean rollback(Params params);

    /**
     * 获取deployment k8s配置
     *
     * @param params 参数
     * @return config
     */
    String getConfigYaml(Params params);

    /**
     * 根据标签或查询所有的deployment集合 kubectl get deployment |grep name labelKey和labelValue为一个过滤键值对
     * 要么同时有值要么同时为空
     *
     * @param params 参数
     * @return Deployments
     */
    List<Deployment> getList(Params params)
    ;

    /**
     * 精准查询deployment kubectl get deployment name
     *
     * @param params 参数
     * @return Deployment
     */
    Deployment getOne(Params params);

    /**
     * 重新部署 deployment kubectl apply -f deployment.yaml
     *
     * @param params 参数
     * @return true/false
     */
    boolean replace(Params params);

    /**
     * 获取部署状态
     *
     * @param params 参数
     * @return 1:进行中 3:失败 4:成功
     */
    DeployStatus getStatus(Params params);

    @Getter
    @Setter
    @Accessors(fluent = true)
    class Params extends KubernetesResource.Params<Params> {

        private String yaml;
        private String image;
        private int replicas;
    }
}
