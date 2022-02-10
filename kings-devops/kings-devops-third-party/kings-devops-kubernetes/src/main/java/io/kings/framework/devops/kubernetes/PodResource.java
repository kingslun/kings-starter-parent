package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import java.util.List;

/**
 * pod资源相关api
 *
 * @author lun.wang
 * @date 2021/8/3 4:12 下午
 * @since v2.0
 */
public interface PodResource extends KubernetesResource<PodResource> {

    /**
     * 根据name删除pod
     *
     * @param name pod名称
     * @return true/false
     */
    boolean delete(String name) throws KubernetesException;

    /**
     * 根据标签查询pods
     *
     * @param label 标签名
     * @return list of pod
     * @see Pod
     */
    List<Pod> findByLabel(String label) throws KubernetesException;

    /**
     * 拉取日志
     *
     * @param podName pod名称
     * @return content of log
     */
    String fetchLog(String podName, String container) throws KubernetesException;
}
