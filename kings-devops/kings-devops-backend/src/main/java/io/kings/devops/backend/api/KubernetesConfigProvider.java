package io.kings.devops.backend.api;

/**
 * k8s配置提供者
 *
 * @author lun.wang
 * @date 2022/2/15 5:33 PM
 * @since v2.3
 */
public interface KubernetesConfigProvider {

    KubernetesDto getByEnvCode(String code);
}
