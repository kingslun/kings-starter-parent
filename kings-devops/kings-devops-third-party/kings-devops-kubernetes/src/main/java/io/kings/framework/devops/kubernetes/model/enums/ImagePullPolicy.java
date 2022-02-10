package io.kings.framework.devops.kubernetes.model.enums;

import io.kings.framework.core.Enumerable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 镜像拉取策略
 *
 * @author lun.wang
 * @date 2022/2/10 4:57 PM
 * @since v2.3
 */
@Getter
@AllArgsConstructor
public enum ImagePullPolicy implements Enumerable<String, String> {
    IF_NOT_PRESENT("IfNotPresent", "优先使用本地镜像"),
    NEVER("Never", "使用本地镜像"),
    ALWAYS("Always", "使用远程镜像");
    private final String code;
    private final String desc;
}
