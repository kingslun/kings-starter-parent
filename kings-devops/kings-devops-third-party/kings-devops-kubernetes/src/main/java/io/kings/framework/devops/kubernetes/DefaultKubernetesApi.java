package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 默认采用fabric8的K8s实现
 *
 * @author lun.wang
 * @date 2021/8/3 4:44 下午
 * @since v2.0
 */
@Slf4j
class DefaultKubernetesApi implements KubernetesApi<KubernetesClient>, BeanClassLoaderAware {
    /**
     * 客户端缓存-操作过得k8s集群都应缓存下来 以便后续操作不重新加载客户端
     */
    final Map<Long, KubernetesClient> clientMap = new ConcurrentHashMap<>();

    private KubernetesClient client(Long id, Supplier<KubernetesClient> clientSupplier) {
        Assert.notNull(id, "kubernetes must had a config id");
        return this.clientMap.computeIfAbsent(id, i -> {
            Objects.requireNonNull(clientSupplier);
            return clientSupplier.get();
        });
    }

    private ClassLoader classLoader;

    /**
     * 代理k8s API的调用 处理log和common exception事宜
     */
    @Slf4j
    private static class KubernetesProxy<S> implements InvocationHandler {
        private final S subclass;

        KubernetesProxy(S subclass) {
            Objects.requireNonNull(subclass);
            this.subclass = subclass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            log.debug("[{}#{}] begin to invoke with:{}", className, methodName, Arrays.toString(args));
            try {
                Object result = method.invoke(subclass, args);
                if (Objects.equals(NamespaceAware.METHOD_NAME, methodName)) {
                    //兼容NamespaceAware的级联操作
                    return proxy;
                } else {
                    return result;
                }
            } catch (InvocationTargetException exception) {
                Throwable target = exception.getTargetException();
                KubernetesException outer;
                if (target instanceof KubernetesException) {
                    outer = (KubernetesException) target;
                } else if (target instanceof KubernetesClientException) {
                    KubernetesClientException k8ser = (KubernetesClientException) target;
                    if (k8ser.getCode() == 404) {
                        outer = new KubernetesResourceNotFoundException();
                    } else {
                        outer = new KubernetesException(target);
                    }
                } else {
                    outer = new KubernetesException(target);
                }
                log.error("[{}#{}] invoked failure cause:{}", className, methodName, outer.getMessage(), outer);
                throw outer;
            }
        }
    }

    @Override
    public PodResource podResource(Long id, Supplier<KubernetesClient> clientSupplier) {
        PodResource podResource = new PodResource.DefaultPodResource(this.client(id, clientSupplier));
        return (PodResource) Proxy.newProxyInstance(this.classLoader, new Class[]{PodResource.class},
                new KubernetesProxy<>(podResource));
    }

    @Override
    public DeploymentResource deploymentResource(Long id, Supplier<KubernetesClient> clientSupplier) {
        DeploymentResource deploymentResource =
                new DeploymentResource.DefaultDeploymentResource(this.client(id, clientSupplier));
        return (DeploymentResource) Proxy.newProxyInstance(this.classLoader, new Class[]{DeploymentResource.class},
                new KubernetesProxy<>(deploymentResource));
    }

    @Override
    public NetworkResource networkResource(Long id, Supplier<KubernetesClient> clientSupplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() {
        log.debug("kubernetes manager is cleaning...");
        this.clientMap.clear();
    }

    @Override
    public void start() throws KubernetesException {
        log.debug("kubernetes manager is running...");
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
