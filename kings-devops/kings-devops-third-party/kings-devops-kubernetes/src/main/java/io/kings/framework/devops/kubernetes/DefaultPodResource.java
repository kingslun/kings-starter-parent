package io.kings.framework.devops.kubernetes;


import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * pod资源的默认实现 采用fabric8客户端实现
 *
 * @author lun.wang
 * @date 2022/2/10 11:04 AM
 * @since v2.3
 */

class DefaultPodResource extends AbstractKubernetesResource<KubernetesClient> implements
        PodResource {

    DefaultPodResource(KubernetesClient client) {
        super(client);
    }

    @Override
    public boolean delete(PodResource.Params params) throws KubernetesException {
        return this.pod(params).delete();
    }

    private io.fabric8.kubernetes.client.dsl.PodResource<Pod> pod(PodResource.Params params) {
        Assert.hasText(params.name(), "resource pod must had a name");
        return Optional.of(client.pods()).map(
                        i -> StringUtils.hasText(params.namespace()) ? i.inNamespace(params.namespace()) : i)
                .map(i -> i.withName(params.name()))
                .orElseThrow(KubernetesResourceNotFoundException::new);
    }

    @Override
    public List<Pod> findByLabel(PodResource.Params params) {
        Assert.notEmpty(params.labels(), "Pod#findByLabel must with labelSelector");
        return Optional.of(client.pods()).map(
                        i -> StringUtils.hasText(params.namespace()) ? i.inNamespace(params.namespace()) : i)
                .map(i -> i.withLabels(params.labels()).list().getItems())
                .orElseThrow(KubernetesResourceNotFoundException::new);
    }

    @Override
    public String fetchLog(PodResource.Params params) {
        return Optional.of(this.pod(params)).map(
                i -> StringUtils.hasText(params.container) ? i.inContainer(params.container).getLog()
                        : i.getLog()).orElseThrow(KubernetesResourceNotFoundException::new);
    }

    @Override
    public void console(PodResource.Params params) {
        Assert.notNull(params.socketIn, "WebSocket must had a in channel");
        Assert.notNull(params.socketOut, "WebSocket must had a out channel");
        Optional.of(this.pod(params))
                .map(i -> StringUtils.hasText(params.container) ? i.inContainer(params.container) : i)
                .orElseThrow(KubernetesResourceNotFoundException::new).readingInput(params.socketIn)
                .writingOutput(params.socketOut).writingError(params.socketOut)
                .usingListener(new WsListener()).exec();
    }
}