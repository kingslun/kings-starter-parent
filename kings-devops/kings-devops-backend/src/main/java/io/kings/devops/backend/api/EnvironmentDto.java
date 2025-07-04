package io.kings.devops.backend.api;

import io.kings.devops.backend.model.EnvironmentDo;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 环境信息
 *
 * @author lun.wang
 * @date 2022/2/14 10:54 AM
 * @since v2.3
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentDto implements Serializable {

    private String code;
    private String description;

    @Override
    public String toString() {
        return "Environment{code='" + code + "', description='" + description + "'}";
    }

    public static EnvironmentDto of(EnvironmentDo environmentDo) {
        if (environmentDo == null) {
            throw new ConfigNotFoundException("no such this environment");
        }
        EnvironmentDto dto = new EnvironmentDto();
        dto.code = environmentDo.getCode();
        dto.description = environmentDo.getDescription();
        return dto;
    }
}
