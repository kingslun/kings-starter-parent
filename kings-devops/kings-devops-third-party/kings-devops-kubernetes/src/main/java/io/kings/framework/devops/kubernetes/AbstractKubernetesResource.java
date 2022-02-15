package io.kings.framework.devops.kubernetes;

import java.util.Objects;
import org.yaml.snakeyaml.Yaml;

/**
 * k8s资源抽象类
 *
 * @author lun.wang
 * @date 2021/8/4 10:43 上午
 * @since v2.0
 */
public abstract class AbstractKubernetesResource<C, S extends KubernetesResource<S>>
    implements KubernetesResource<S> {

    protected final Yaml yaml;
    protected String namespace;
    protected final C client;

    protected AbstractKubernetesResource(C client) {
        Objects.requireNonNull(client);
        this.client = client;
        this.yaml = new Yaml();
    }

    @Override
    @SuppressWarnings("unchecked")
    public S withNamespace(String namespace) {
        this.namespace = namespace;
        return (S) this;
    }
}
