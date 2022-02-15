package io.kings.devops.backend.api.impl;

import io.kings.devops.backend.api.KubernetesConfigApi;
import io.kings.devops.backend.api.KubernetesDto;
import io.kings.devops.backend.dao.KubernetesDao;
import lombok.AllArgsConstructor;

/**
 * k8s信息业务实现
 *
 * @author lun.wang
 * @date 2022/2/14 10:42 AM
 * @since v2.3
 */
@AllArgsConstructor
class KubernetesConfigApiImpl implements KubernetesConfigApi {

    private final KubernetesDao kubernetesDao;

    @Override
    public KubernetesDto getByEnvCode(String code) {
        return KubernetesDto.of(this.kubernetesDao.findByEnvCode(code));
    }
}