package io.kings.devops.backend.api;

/**
 * 环境信息业务
 *
 * @author lun.wang
 * @date 2022/2/14 10:42 AM
 * @since v2.3
 */
public interface EnvironmentApi {

    EnvironmentDto getByCode(String code);
}
