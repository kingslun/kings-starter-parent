package io.kings.framework.devops.kubernetes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * pod描述对象
 *
 * @author lun.wang
 * @date 2022/2/10 5:58 PM
 * @since v2.3
 */
@Getter
@Setter
@Accessors(fluent = true)
public class PodTemplateSpec {

    private Metadata metadata;
    private PodSpec spec;
}
