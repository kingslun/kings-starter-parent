package io.kings.devops.backend.api.impl;

import io.kings.devops.backend.api.DockerHarborApi;
import io.kings.devops.backend.dao.DockerHarborDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * docker仓库业务实现
 *
 * @author lun.wang
 * @date 2022/2/14 10:42 AM
 * @since v2.3
 */
@Service
@AllArgsConstructor
class DockerHarborImpl implements DockerHarborApi {

    private final DockerHarborDao dockerHarborDao;

}