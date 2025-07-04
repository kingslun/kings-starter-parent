package io.kings.framework.devops.kubernetes.model.enums;

import io.kings.framework.core.Enumerable;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 部署状态 1 进行中 3 失败 4 成功
 *
 * @author lun.wang
 * @date 2022/2/10 4:35 PM
 * @since v2.3
 */
@Getter
@AllArgsConstructor
public enum DeployStatus implements Enumerable<Integer, String> {
    SUCCESS(4, "成功"), FAILURE(3, "失败"), ONGOING(1, "进行中");
    private final Integer code;
    private final String desc;

    public static DeployStatus of(int code) {
        return Arrays.stream(DeployStatus.values()).filter(i -> i.getCode() == code).findFirst()
                .orElseThrow(KubernetesDeploymentStatusNotFoundException::new);
    }

    static class KubernetesDeploymentStatusNotFoundException extends KubernetesException {

        public KubernetesDeploymentStatusNotFoundException() {
            super("no such status for deployment");
        }
    }
}