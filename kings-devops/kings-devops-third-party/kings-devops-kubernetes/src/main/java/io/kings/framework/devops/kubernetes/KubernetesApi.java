package io.kings.framework.devops.kubernetes;

/**
 * @author lun.wang
 * @date 2022/2/15 6:07 PM
 * @since v2.3
 */
public interface KubernetesApi extends AutoCloseable{

    PodResource podResource();

    DeploymentResource deploymentResource();

    NetworkResource networkResource();
}
