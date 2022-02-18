package io.kings.framework.devops.kubernetes;

import static io.kings.framework.devops.kubernetes.KubernetesResource.NAMESPACE_METHOD;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.kings.devops.backend.api.KubernetesConfigProvider;
import io.kings.devops.backend.api.KubernetesDto;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * 默认采用fabric8的K8s实现
 *
 * @author lun.wang
 * @date 2021/8/3 4:44 下午
 * @since v2.0
 */
@Slf4j
public final class CachedKubernetesApiFactory implements KubernetesApiFactory,
    BeanClassLoaderAware {

    private final KubernetesConfigProvider configProvider;

    public CachedKubernetesApiFactory(KubernetesConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    /**
     * 客户端缓存-操作过得k8s集群都应缓存下来 以便后续操作不重新加载客户端
     */
    final Map<String, KubernetesApi> clientMap = new HashMap<>(16);
    private ClassLoader classLoader;

    /**
     * 代理k8s API的调用 处理log和common exception事宜
     */
    @Slf4j
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class KubernetesProxy<D> implements InvocationHandler {

        private final D delegate;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            log.debug("[{}#{}] begin to invoke with:{}", className, methodName,
                Arrays.toString(args));
            try {
                Object result = method.invoke(delegate, args);
                if (Objects.equals(NAMESPACE_METHOD, methodName)) {
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
                log.error("[{}#{}] invoked failure cause:{}", className, methodName,
                    outer.getMessage(), outer);
                throw outer;
            }
        }
    }

    @AllArgsConstructor
    private static class DefaultKubernetesApi implements KubernetesApi {

        private final KubernetesClient client;
        private final ClassLoader classLoader;

        @Override
        public PodResource podResource() {
            PodResource podResource = new DefaultPodResource(this.client);
            return (PodResource) Proxy.newProxyInstance(classLoader, new Class[]{PodResource.class},
                new KubernetesProxy<>(podResource));
        }

        @Override
        public DeploymentResource deploymentResource() {
            DeploymentResource deploymentResource = new DefaultDeploymentResource(this.client);
            return (DeploymentResource) Proxy.newProxyInstance(this.classLoader,
                new Class[]{DeploymentResource.class}, new KubernetesProxy<>(deploymentResource));
        }

        @Override
        public NetworkResource networkResource() {
            NetworkResource networkResource = new DefaultNetworkResource(this.client);
            return (NetworkResource) Proxy.newProxyInstance(this.classLoader,
                new Class[]{NetworkResource.class}, new KubernetesProxy<>(networkResource));
        }

        @Override
        public void close() {
            this.client.close();
        }

    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public KubernetesApi instance(String env) {
        return this.clientMap.computeIfAbsent(env, code -> {
            Assert.notNull(code, "kubernetes must had a env code");
            KubernetesDto dto = this.configProvider.getByEnvCode(code);
            Config config = new ConfigBuilder().withMasterUrl(dto.getAccessUrl())
                .withOauthToken(dto.getAccessToken()).withTrustCerts(true).build();
            return new DefaultKubernetesApi(new DefaultKubernetesClient(config), this.classLoader);
        });
    }
}
