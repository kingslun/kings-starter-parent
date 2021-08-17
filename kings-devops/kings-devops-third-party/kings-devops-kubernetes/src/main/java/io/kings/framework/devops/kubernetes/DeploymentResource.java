package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import io.kings.framework.devops.kubernetes.model.Deployment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * deployment资源api
 *
 * @author lun.wang
 * @date 2021/8/3 4:12 下午
 * @since v2.0
 */
public interface DeploymentResource extends NamespaceAware<DeploymentResource> {
    class DefaultDeploymentResource extends AbstractKubernetesResource<KubernetesClient, DeploymentResource>
            implements DeploymentResource {
        private final Yaml yaml;

        DefaultDeploymentResource(KubernetesClient client) {
            super(client);
            this.yaml = new Yaml();
        }

        @Override
        public boolean delete(String name) throws KubernetesException {
            return this.resource(name).delete();
        }

        @Override
        public boolean scale(String name, int replicas) {
            return this.resource(name).scale(replicas) != null;
        }

        @Override
        public boolean restart(String name) throws KubernetesException {
            // modify /spec/template/metadata/labels/updatedTimestamp
            RollableScalableResource<io.fabric8.kubernetes.api.model.apps.Deployment, ?> resource = this.resource(name);
            final io.fabric8.kubernetes.api.model.apps.Deployment deployment = this.supplierWrapper(resource).get();
            final Map<String, String> labels = deployment.getSpec().getTemplate().getMetadata().getLabels();
            labels.put("updatedTimestamp", String.valueOf(System.currentTimeMillis()));
            return resource.patch(deployment) != null;
        }

        @Override
        public boolean rollback(String name, String image) {
            Assert.hasText(image, "rollback deployment must had an image");
            RollableScalableResource<io.fabric8.kubernetes.api.model.apps.Deployment, ?> resource = this.resource(name);
            final io.fabric8.kubernetes.api.model.apps.Deployment deployment = this.supplierWrapper(resource).get();
//          /spec/template/spec/containers/0/image
            deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setImage(image);
            return resource.patch(deployment) != null;
        }

        @Override
        public String getConfigYaml(String name) {
            return this.yaml.dump(this.supplier(name).get());
        }

        @Override
        public List<Deployment> getList(String labelKey, String labelValue) {
            DeploymentList deploymentList;
            if (StringUtils.hasText(labelKey)) {
                deploymentList =
                        super.client.apps().deployments().inNamespace(super.namespace).withLabel(labelKey, labelValue)
                                .list();
            } else {
                deploymentList = super.client.apps().deployments().inNamespace(super.namespace).list();
            }
            if (deploymentList == null || CollectionUtils.isEmpty(deploymentList.getItems())) {
                return Collections.emptyList();
            }
            return deploymentList.getItems().stream().map(this::convert).collect(Collectors.toList());
        }

        private Deployment convert(io.fabric8.kubernetes.api.model.apps.Deployment item) {
            Assert.notNull(item, "Deployment is null");
            final ObjectMeta metadata = item.getMetadata();
            final DeploymentStatus status = item.getStatus();
            final DeploymentSpec spec = item.getSpec();
            Deployment deployment = new Deployment();
            List<Container> containers = spec.getTemplate().getSpec().getContainers();
            for (Container container : containers) {
                if (container.getName().equals(metadata.getName())) {
                    deployment.setImage(container.getImage());
                    break;
                }
            }
            deployment.setName(metadata.getName());
            deployment.setCreationTime(super.convert(metadata.getCreationTimestamp()));
            deployment.setVersion(metadata.getLabels().get("version_id"));
            deployment.setLabels(metadata.getLabels());
            deployment.setAnnotations(metadata.getAnnotations());
            deployment.setNamespace(metadata.getNamespace());
            deployment.setReplicas(spec.getReplicas());
            deployment.setMatchLabels(spec.getSelector().getMatchLabels());
            deployment.setType(spec.getStrategy().getType());
            deployment.setMaxSurge(spec.getStrategy().getRollingUpdate().getMaxSurge().getStrVal());
            deployment.setMaxUnavailable(spec.getStrategy().getRollingUpdate().getMaxUnavailable().getStrVal());
            deployment.setUpdatedReplicas(status.getUpdatedReplicas());
            deployment.setReadyReplicas(status.getReadyReplicas());
            deployment.setUnavailableReplicas(status.getUnavailableReplicas());
            deployment.setAvailableReplicas(status.getAvailableReplicas());
            return deployment;
        }

        @Override
        public Deployment getOne(String name) throws KubernetesException {
            return this.convert(this.supplier(name).get());
        }

        /**
         * get k8s deployment,if null will throw KubernetesResourceNotFoundException
         *
         * @param name deployment name
         * @return Supplier
         */
        private Supplier<io.fabric8.kubernetes.api.model.apps.Deployment> supplier(String name) {
            return supplierWrapper(this.resource(name));
        }

        private Supplier<io.fabric8.kubernetes.api.model.apps.Deployment> supplierWrapper(
                RollableScalableResource<io.fabric8.kubernetes.api.model.apps.Deployment, ?> resource) {
            return () -> Optional.ofNullable(resource.get()).orElseThrow(KubernetesResourceNotFoundException::new);
        }

        /**
         * 根据name从K8s集群获取deployment
         *
         * @param name deployment name
         * @return resource of fabric8
         */
        private RollableScalableResource<io.fabric8.kubernetes.api.model.apps.Deployment, ?> resource(String name) {
            Assert.hasText(name, "query deployment must had an name");
            return super.client.apps().deployments().inNamespace(super.namespace).withName(name);
        }

        @Override
        public boolean replace(String name, String yaml) throws KubernetesException {
            return this.resource(name)
                    .replace(this.yaml.loadAs(yaml, io.fabric8.kubernetes.api.model.apps.Deployment.class)) != null;
        }

        @Override
        public Deployment.Status getStatus(String name) throws KubernetesException {
            final DeploymentStatus status = this.supplier(name).get().getStatus();
            // 副本数
            Integer replicas = status.getReplicas();
            // 可用副本数
            Integer availableReplicas = status.getAvailableReplicas();
            // 已经准备好的副本数
            Integer readyReplicas = status.getReadyReplicas();
            if (Objects.equals(replicas, availableReplicas) && Objects.equals(availableReplicas, readyReplicas)) {
                return Deployment.Status.SUCCESS;
            } else {
                return Deployment.Status.ONGOING;
            }
        }
    }

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
     * 根据标签或查询所有的deployment集合
     * kubectl get deployment |grep name
     * labelKey和labelValue为一个过滤键值对 要么同时有值要么同时为空
     *
     * @param labelKey   标签键
     * @param labelValue 标签值
     * @return Deployments
     */
    List<Deployment> getList(String labelKey, String labelValue) throws KubernetesException;

    /**
     * 精准查询deployment
     * kubectl get deployment name
     *
     * @param name name
     * @return Deployment
     */
    Deployment getOne(String name) throws KubernetesException;

    /**
     * 重新部署 deployment
     * kubectl apply -f deployment.yaml
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
    Deployment.Status getStatus(String name) throws KubernetesException;
}
