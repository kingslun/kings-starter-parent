package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.Gettable;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import java.util.Objects;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

/**
 * k8s资源抽象类
 *
 * @author lun.wang
 * @date 2021/8/4 10:43 上午
 * @since v2.0
 */
public abstract class AbstractKubernetesResource<C> implements KubernetesResource {

    protected final Yaml yaml;
    protected final C client;

    protected AbstractKubernetesResource(C client) {
        Objects.requireNonNull(client);
        this.client = client;
        this.yaml = new Yaml();
    }

    protected <T> T get(Gettable<T> gettable) {
        Assert.notNull(gettable, "NullPointException");
        try {
            T t = gettable.get();
            if (t == null) {
                throw new KubernetesResourceNotFoundException();
            }
            return t;
        } catch (KubernetesClientException e) {
            throw new KubernetesException(e);
        }
    }
}
