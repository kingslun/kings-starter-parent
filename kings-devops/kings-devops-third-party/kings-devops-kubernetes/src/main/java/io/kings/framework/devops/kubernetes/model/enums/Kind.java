package io.kings.framework.devops.kubernetes.model.enums;

import io.kings.framework.core.Enumerable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * kubernetes资源种类
 *
 * @author lun.wang
 * @date 2022/2/10 4:10 PM
 * @since v2.3
 */
@Getter
@AllArgsConstructor
public enum Kind implements Enumerable<String, String> {
    POD("Pod", "最小单元"),
    DEPLOYMENT("Deployment", "部署计划"),
    SERVICE("Service", "service"),
    INGRESS("Ingress", "ingress");
    private final String code;
    private final String desc;
}
