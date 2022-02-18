package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import java.util.List;
import org.springframework.util.Assert;

/**
 * pod资源的默认实现 采用fabric8客户端实现
 *
 * @author lun.wang
 * @date 2022/2/10 11:04 AM
 * @since v2.3
 */

class DefaultPodResource
    extends AbstractKubernetesResource<KubernetesClient, PodResource>
    implements PodResource {

    DefaultPodResource(KubernetesClient client) {
        super(client);
    }

    @Override
    public boolean delete(String name) throws KubernetesException {
        Assert.hasText(name, "delete pod must had a name");
        return client.pods().inNamespace(super.namespace).withName(name).delete();
    }

    @Override
    public List<Pod> findByLabel(String label) {
        return client.pods().inNamespace(super.namespace).withLabel(label).list().getItems();
    }

    @Override
    public String fetchLog(String podName, String container) {
        return client.pods().inNamespace(super.namespace).withName(podName)
            .inContainer(container).getLog();
    }
}