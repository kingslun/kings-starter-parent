package io.kings.devops.backend.api;

import io.kings.devops.backend.model.EnvironmentDo;
import java.io.Serializable;
import java.util.NoSuchElementException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 环境信息
 *
 * @author lun.wang
 * @date 2022/2/14 10:54 AM
 * @since v2.3
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentDto implements Serializable {

    private String code;
    private String description;

    public static EnvironmentDto of(EnvironmentDo environmentDo) {
        if (environmentDo == null) {
            throw new NoSuchElementException("no such this environment");
        }
        EnvironmentDto dto = new EnvironmentDto();
        dto.code = environmentDo.getCode();
        dto.description = environmentDo.getDescription();
        return dto;
    }
}
