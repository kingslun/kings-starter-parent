package io.kings.devops.backend.api.impl;

import io.kings.devops.backend.api.DockerHarborConfigApi;
import io.kings.devops.backend.api.EnvironmentConfigApi;
import io.kings.devops.backend.api.KubernetesConfigApi;
import io.kings.devops.backend.dao.DockerHarborDao;
import io.kings.devops.backend.dao.EnvironmentDao;
import io.kings.devops.backend.dao.KubernetesDao;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author lun.wang
 * @date 2022/2/15 5:40 PM
 * @since v2.3
 */
@EnableJpaRepositories(basePackageClasses = DockerHarborDao.class)
class MetadataConfigAutoConfiguration {

    @Bean
    DockerHarborConfigApi dockerHarborConfigApi(DockerHarborDao dockerHarborDao) {
        return new DockerHarborConfigApiImpl(dockerHarborDao);
    }

    @Bean
    EnvironmentConfigApi environmentConfigApi(EnvironmentDao environmentDao) {
        return new EnvironmentConfigApiImpl(environmentDao);
    }

    @Bean
    KubernetesConfigApi kubernetesConfigApi(KubernetesDao kubernetesDao) {
        return new KubernetesConfigApiImpl(kubernetesDao);
    }
}
