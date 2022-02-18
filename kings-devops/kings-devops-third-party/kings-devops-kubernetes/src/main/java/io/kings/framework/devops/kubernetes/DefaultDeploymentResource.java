package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import io.kings.framework.devops.kubernetes.model.enums.DeployStatus;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * deployment资源操作
 *
 * @author lun.wang
 * @date 2022/2/10 11:08 AM
 * @since v2.3
 */
class DefaultDeploymentResource extends
    AbstractKubernetesResource<KubernetesClient, DeploymentResource>
    implements DeploymentResource {

    DefaultDeploymentResource(KubernetesClient client) {
        super(client);
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
        RollableScalableResource<Deployment> resource = this.resource(name);
        final Deployment deployment = this.supplierWrapper(resource).get();
        final Map<String, String> labels = deployment.getSpec().getTemplate().getMetadata()
            .getLabels();
        labels.put("updatedTimestamp", String.valueOf(System.currentTimeMillis()));
        return resource.patch(deployment) != null;
    }

    @Override
    public boolean rollback(String name, String image) {
        Assert.hasText(image, "rollback deployment must had an image");
        RollableScalableResource<Deployment> resource = this.resource(name);
        final Deployment deployment = this.supplierWrapper(resource).get();
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
            deploymentList = super.client.apps().deployments().inNamespace(super.namespace)
                .withLabel(labelKey, labelValue).list();
        } else {
            deploymentList = super.client.apps().deployments().inNamespace(super.namespace).list();
        }
        if (deploymentList == null || CollectionUtils.isEmpty(deploymentList.getItems())) {
            return Collections.emptyList();
        }
        return deploymentList.getItems();
    }

    @Override
    public Deployment getOne(String name) throws KubernetesException {
        return this.supplier(name).get();
    }

    /**
     * get k8s deployment,if null will throw KubernetesResourceNotFoundException
     *
     * @param name deployment name
     * @return Supplier
     */
    private Supplier<Deployment> supplier(String name) {
        return supplierWrapper(this.resource(name));
    }

    private Supplier<Deployment> supplierWrapper(RollableScalableResource<Deployment> resource) {
        return () -> Optional.ofNullable(resource.get())
            .orElseThrow(KubernetesResourceNotFoundException::new);
    }

    /**
     * 根据name从K8s集群获取deployment
     *
     * @param name deployment name
     * @return resource of fabric8
     */
    private RollableScalableResource<Deployment> resource(String name) {
        Assert.hasText(name, "query deployment must had an name");
        return super.client.apps().deployments().inNamespace(super.namespace).withName(name);
    }

    @Override
    public boolean replace(String name, String yaml) throws KubernetesException {
        return this.resource(name).replace(super.yaml.loadAs(yaml, Deployment.class)) != null;
    }

    @Override
    public DeployStatus getStatus(String name) throws KubernetesException {
        final DeploymentStatus status = this.supplier(name).get().getStatus();
        // 副本数
        Integer replicas = status.getReplicas();
        // 可用副本数
        Integer availableReplicas = status.getAvailableReplicas();
        // 已经准备好的副本数
        Integer readyReplicas = status.getReadyReplicas();
        if (Objects.equals(replicas, availableReplicas) && Objects.equals(availableReplicas,
            readyReplicas)) {
            return DeployStatus.SUCCESS;
        } else {
            return DeployStatus.ONGOING;
        }
    }
}