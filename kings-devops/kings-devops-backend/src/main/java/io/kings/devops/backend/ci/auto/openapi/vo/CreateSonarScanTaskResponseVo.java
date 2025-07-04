package io.kings.devops.backend.ci.auto.openapi.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lun.wang
 * @date 2022/3/21 3:56 PM
 * @since v2.5
 */
@Getter
@Setter
@ToString
public class CreateSonarScanTaskResponseVo implements Serializable {

    private Boolean status;
    private String information;
}
