package io.kings.devops.backend.api.impl;

import io.kings.devops.backend.api.EnvironmentConfigApi;
import io.kings.devops.backend.api.EnvironmentDto;
import io.kings.devops.backend.dao.EnvironmentDao;
import lombok.AllArgsConstructor;

/**
 * 环境信息业务
 *
 * @author lun.wang
 * @date 2022/2/14 10:42 AM
 * @since v2.3
 */
@AllArgsConstructor
class EnvironmentConfigApiImpl implements EnvironmentConfigApi {

    private final EnvironmentDao environmentDao;

    @Override
    public EnvironmentDto findByCode(String code) {
        return EnvironmentDto.of(this.environmentDao.findByCode(code));
    }
}
