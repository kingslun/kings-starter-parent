package io.kings.devops.backend.api.impl;

import io.kings.devops.backend.api.DockerHarborConfigApi;
import io.kings.devops.backend.dao.DockerHarborDao;
import lombok.AllArgsConstructor;

/**
 * docker仓库业务实现
 *
 * @author lun.wang
 * @date 2022/2/14 10:42 AM
 * @since v2.3
 */
@AllArgsConstructor
class DockerHarborConfigApiImpl implements DockerHarborConfigApi {

    private final DockerHarborDao dockerHarborDao;

}