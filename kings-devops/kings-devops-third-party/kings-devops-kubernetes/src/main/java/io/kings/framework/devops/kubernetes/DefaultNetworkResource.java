package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * pod资源的默认实现 采用fabric8客户端实现
 *
 * @author lun.wang
 * @date 2022/2/10 11:04 AM
 * @since v2.3
 */

class DefaultNetworkResource extends
    AbstractKubernetesResource<KubernetesClient> implements NetworkResource {

    DefaultNetworkResource(KubernetesClient client) {
        super(client);
    }

    @Override
    public void createIngress() {
        throw new UnsupportedOperationException();
    }

    static class DefaultServiceResource extends
        AbstractKubernetesResource<KubernetesClient> implements ServiceResource {

        DefaultServiceResource(KubernetesClient client) {
            super(client);
        }

        @Override
        public void createService(String namespace) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object status(String namespace,String name) {
            return get(client.services().inNamespace(namespace).withName(name)).getStatus();
        }
    }

    private final ServiceResource svc = new DefaultServiceResource(this.client);

    @Override
    public ServiceResource svc() {
        return this.svc;
    }
}