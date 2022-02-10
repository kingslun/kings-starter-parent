package io.kings.framework.devops.kubernetes.model.enums;

import io.kings.framework.core.Enumerable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 部署策略类型
 *
 * @author lun.wang
 * @date 2022/2/10 2:24 PM
 * @since v2.3
 */
@Getter
@AllArgsConstructor
public enum DeployStrategyType implements Enumerable<String, String> {
    /**
     * 先删除再部署 节省资源
     */
    RECREATE("Recreate", "Recreate"),
    /**
     * 滚动升级 具体滚动策略依赖其它配置项
     */
    ROLLING_UPDATE("RollingUpdate", "RollingUpdate");
    private final String code;
    private final String desc;
}
