package io.kings.devops.backend.api.impl;

import io.kings.devops.backend.api.EnvironmentApi;
import io.kings.devops.backend.api.EnvironmentDto;
import io.kings.devops.backend.dao.EnvironmentDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 环境信息业务
 *
 * @author lun.wang
 * @date 2022/2/14 10:42 AM
 * @since v2.3
 */
@Service
@AllArgsConstructor
class EnvironmentApiImpl implements EnvironmentApi {

    private final EnvironmentDao environmentDao;

    @Override
    public EnvironmentDto getByCode(String code) {
        return EnvironmentDto.of(this.environmentDao.findByCode(code));
    }
}
