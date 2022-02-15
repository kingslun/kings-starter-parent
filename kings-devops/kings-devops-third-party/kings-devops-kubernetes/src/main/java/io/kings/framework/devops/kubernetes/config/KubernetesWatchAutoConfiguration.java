package io.kings.framework.devops.kubernetes.config;

import io.kings.devops.backend.api.KubernetesConfigProvider;
import io.kings.framework.core.bean.BeanLifecycle;
import io.kings.framework.core.exception.BeanLifecycleException;
import io.kings.framework.devops.kubernetes.KubernetesApiFactory;
import io.kings.framework.devops.kubernetes.fabric8.CachedKubernetesApiFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * 监控自动装配
 *
 * @author your name
 * @date 2022/2/10 10:52 AM
 * @since v2.3
 */
class KubernetesWatchAutoConfiguration implements BeanLifecycle {

    @Override
    public void complete() throws BeanLifecycleException {
        BeanLifecycle.super.complete();
    }

    @Bean
    @ConditionalOnBean(KubernetesConfigProvider.class)
    KubernetesApiFactory kubernetesApiFactory(KubernetesConfigProvider configProvider) {
        return new CachedKubernetesApiFactory(configProvider);
    }
}
