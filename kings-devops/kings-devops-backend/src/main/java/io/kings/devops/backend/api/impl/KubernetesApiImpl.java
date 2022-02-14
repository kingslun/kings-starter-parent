package io.kings.devops.backend.api.impl;

import io.kings.devops.backend.api.KubernetesApi;
import io.kings.devops.backend.dao.KubernetesDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * k8s信息业务实现
 *
 * @author lun.wang
 * @date 2022/2/14 10:42 AM
 * @since v2.3
 */
@Service
@AllArgsConstructor
class KubernetesApiImpl implements KubernetesApi {

    private final KubernetesDao kubernetesDao;

}