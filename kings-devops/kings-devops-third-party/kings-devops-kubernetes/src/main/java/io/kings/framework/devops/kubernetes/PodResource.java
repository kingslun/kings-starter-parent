package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerState;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.model.Pod;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * pod资源相关api
 *
 * @author lun.wang
 * @date 2021/8/3 4:12 下午
 * @since v2.0
 */
public interface PodResource extends NamespaceAware<PodResource> {
    /**
     * 根据name删除pod
     *
     * @param name pod名称
     * @return true/false
     */
    boolean delete(String name) throws KubernetesException;

    /**
     * 根据标签查询pods
     *
     * @param label 标签名
     * @return list of pod
     * @see Pod
     */
    List<Pod> findByLabel(String label) throws KubernetesException;

    /**
     * 拉取日志
     *
     * @param podName pod名称
     * @return content of log
     */
    String fetchLog(String podName, String container) throws KubernetesException;

    /**
     * pod资源的默认实现 采用fabric8客户端实现
     */
    class DefaultPodResource extends AbstractKubernetesResource<KubernetesClient, PodResource> implements PodResource {

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
            return convert(client.pods().inNamespace(super.namespace).withLabel(label).list().getItems());
        }

        private List<Pod> convert(List<io.fabric8.kubernetes.api.model.Pod> pods) {
            if (CollectionUtils.isEmpty(pods)) {
                return Collections.emptyList();
            }
            List<Pod> returned = new ArrayList<>(pods.size());
            for (io.fabric8.kubernetes.api.model.Pod v1Pod : pods) {
                Pod pod1 = new Pod();
                pod1.setCreationTime(super.convert(v1Pod.getMetadata().getCreationTimestamp()));
                pod1.setPodName(v1Pod.getMetadata().getName());
                pod1.setPodIp(v1Pod.getStatus().getPodIP());
                pod1.setNodeName(v1Pod.getSpec().getNodeName());
                pod1.setHostIp(v1Pod.getStatus().getHostIP());
                pod1.setPhase(v1Pod.getStatus().getPhase());
                final List<Container> containers = v1Pod.getSpec().getContainers();
                List<Pod.Container> podContainers = new ArrayList<>(containers.size());
                containers.forEach(x -> {
                    Pod.Container container = new Pod.Container();
                    container.setContainerName(x.getName());
                    container.setImage(x.getImage());
                    container.setImagePullPolicy(x.getImagePullPolicy());
                    container.setWorkingDir(x.getWorkingDir());
                    podContainers.add(container);
                });
                pod1.setContainers(podContainers);
                List<ContainerStatus> containerStatusList = v1Pod.getStatus().getContainerStatuses();
                if (!CollectionUtils.isEmpty(containerStatusList)) {
                    ContainerStatus containerStatus = containerStatusList.get(0);
                    pod1.setRestartCount(containerStatus.getRestartCount());
                    pod1.setPhase(Objects.equals(Boolean.TRUE, containerStatus.getReady()) ? "Running" :
                            "Pod状态：" + pod1.getPhase() + ", 容器状态：Terminating");
                    ContainerState lastState = containerStatus.getLastState();
                    if (lastState == null) {
                        pod1.setLastTerminatingPhase("");
                    } else {
                        pod1.setLastTerminatingPhase(
                                lastState.getTerminated() == null ? "" : lastState.getTerminated().getReason());
                    }
                }
                returned.add(pod1);
            }
            return returned;
        }

        @Override
        public String fetchLog(String podName, String container) {
            return client.pods().inNamespace(super.namespace).withName(podName).inContainer(container).getLog();
        }
    }
}
