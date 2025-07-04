package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import io.kings.framework.devops.kubernetes.model.enums.DeployStatus;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * deployment资源操作
 *
 * @author lun.wang
 * @date 2022/2/10 11:08 AM
 * @since v2.3
 */
class DefaultDeploymentResource extends
        AbstractKubernetesResource<KubernetesClient> implements DeploymentResource {

    DefaultDeploymentResource(KubernetesClient client) {
        super(client);
    }

    @Override
    public boolean delete(DeploymentResource.Params params) {
        return this.resourceOpt(params).delete();
    }

    @Override
    public boolean scale(DeploymentResource.Params params) {
        if (params.replicas() < 0) {
            throw new KubernetesException("Invalid replicas parameter");
        }
        return this.resourceOpt(params).scale(params.replicas()) != null;
    }

    @Override
    public boolean restart(DeploymentResource.Params params) {
        // modify /spec/template/metadata/labels/updatedTimestamp
        RollableScalableResource<Deployment> resource = this.resourceOpt(params);
        final Deployment deployment = get(resource);
        final Map<String, String> labels = deployment.getSpec().getTemplate().getMetadata()
                .getLabels();
        labels.put("updatedTimestamp", String.valueOf(System.currentTimeMillis()));
        return resource.patch(deployment) != null;
    }

    @Override
    public boolean rollback(DeploymentResource.Params params) {
        Assert.hasText(params.image(), "rollback deployment must had an image");
        RollableScalableResource<Deployment> resourceOpt = this.resourceOpt(params);
        final Deployment deployment = get(resourceOpt);
//          /spec/template/spec/containers/0/image
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0)
                .setImage(params.image());
        return resourceOpt.patch(deployment) != null;
    }

    @Override
    public String getConfigYaml(DeploymentResource.Params params) {
        return this.yaml.dump(this.resource(params));
    }

    @Override
    public List<Deployment> getList(DeploymentResource.Params params) {
        DeploymentList deploymentList = Optional.of(client.apps().deployments()).map(
                        i -> StringUtils.hasText(params.namespace()) ? i.inNamespace(params.namespace()) : i)
                .map(
                        i -> CollectionUtils.isEmpty(params.labels()) ? i.withLabels(params.labels()).list()
                                : i.list()).orElseThrow(KubernetesResourceNotFoundException::new);
        if (deploymentList == null || CollectionUtils.isEmpty(deploymentList.getItems())) {
            return Collections.emptyList();
        }
        return deploymentList.getItems();
    }

    @Override
    public Deployment getOne(DeploymentResource.Params params) {
        return this.resource(params);
    }

    /**
     * 根据name从K8s集群获取deployment
     *
     * @return resource of fabric8
     */
    private Deployment resource(DeploymentResource.Params params) {
        return get(resourceOpt(params));
    }

    private RollableScalableResource<Deployment> resourceOpt(DeploymentResource.Params params) {
        Assert.hasText(params.name(), "query deployment must had an name");
        return Optional.of(client.apps().deployments()).map(
                        i -> StringUtils.hasText(params.namespace()) ? i.inNamespace(params.namespace()) : i)
                .map(i -> i.withName(params.name()))
                .orElseThrow(KubernetesResourceNotFoundException::new);
    }

    @Override
    public boolean replace(DeploymentResource.Params params) {
        Assert.hasText(params.yaml(), "replace must had yaml config");
        return this.resourceOpt(params).replace(this.yaml.loadAs(params.yaml(), Deployment.class))
                != null;
    }

    @Override
    public DeployStatus getStatus(DeploymentResource.Params params) {
        final DeploymentStatus status = this.resource(params).getStatus();
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