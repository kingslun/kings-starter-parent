package io.kings.framework.devops.kubernetes;

import java.util.Objects;

/**
 * k8s资源抽象类
 *
 * @author lun.wang
 * @date 2021/8/4 10:43 上午
 * @since v2.0
 */
abstract class AbstractKubernetesResource<C, S> implements NamespaceAware<S> {

    protected String namespace;
    protected final C client;

    AbstractKubernetesResource(C client) {
        Objects.requireNonNull(client);
        this.client = client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S namespace(String namespace) {
        this.namespace = namespace;
        return (S) this;
    }
}
