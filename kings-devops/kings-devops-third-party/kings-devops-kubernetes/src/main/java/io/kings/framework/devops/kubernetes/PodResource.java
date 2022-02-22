package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import java.io.InputStream;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

/**
 * pod资源相关api
 *
 * @author lun.wang
 * @date 2021/8/3 4:12 下午
 * @since v2.0
 */
public interface PodResource extends KubernetesResource {

    /**
     * 根据name删除pod
     *
     * @param params 参数
     * @return true/false
     */
    boolean delete(Params params);

    /**
     * 根据标签查询pods
     *
     * @param params 参数
     * @return list of pod
     * @see Pod
     */
    List<Pod> findByLabel(Params params);

    /**
     * 拉取日志
     *
     * @param params 参数
     * @return content of log
     */
    String fetchLog(Params params);

    void shell(Params params);

    @Getter
    @Setter
    @Accessors(fluent = true)
    class Params extends KubernetesResource.Params<Params> {

        @Nullable
        String container;
        @Nullable
        transient InputStream in;
    }
}
