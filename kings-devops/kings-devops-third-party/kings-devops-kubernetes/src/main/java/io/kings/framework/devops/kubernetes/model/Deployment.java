package io.kings.framework.devops.kubernetes.model;

import io.kings.framework.data.Enumerable;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Map;

/**
 * kubernetes deployment描述对象
 *
 * @author lun.wang
 * @date 2021-08-07
 * @since v1.0
 */
@Getter
@Setter
@ToString
public class Deployment {
    /**
     * 名称
     */
    private String name;
    /**
     * 副本数
     */
    private Integer replicas;
    /**
     * 版本
     */
    private String version;
    /**
     * 镜像
     */
    private String image;
    /**
     * 创建时间
     */
    private long creationTime;
    /**
     * 标签
     */
    private Map<String, String> labels;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 注解
     */
    private Map<String, String> annotations;
    /**
     * 选择器
     */
    private Map<String, String> matchLabels;
    /**
     * 策略
     */
    private String type;
    /**
     * 超过期望的Pod数量
     */
    private String maxSurge;
    /**
     * 不可用Pod最大数量
     */
    private String maxUnavailable;
    /**
     * 已更新副本数
     */
    private Integer updatedReplicas;
    /**
     * 计划更新副本数
     */
    private Integer readyReplicas;
    /**
     * 不可用副本数
     */
    private Integer unavailableReplicas;
    /**
     * 可用副本数
     */
    private Integer availableReplicas;

    /**
     * 部署状态 1 进行中 3 失败 4 成功
     */
    public enum Status implements Enumerable<Integer, String> {
        SUCCESS(4, "成功"), FAILURE(3, "失败"), ONGOING(1, "进行中");
        @Getter
        private final Integer code;
        @Getter
        private final String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static Status of(int code) {
            return Arrays.stream(Status.values()).filter(i -> i.getCode() == code).findFirst()
                    .orElseThrow(KubernetesDeploymentStatusNotFoundException::new);
        }

        static class KubernetesDeploymentStatusNotFoundException extends KubernetesException {

            public KubernetesDeploymentStatusNotFoundException() {
                super("no such status for deployment");
            }
        }
    }
}
