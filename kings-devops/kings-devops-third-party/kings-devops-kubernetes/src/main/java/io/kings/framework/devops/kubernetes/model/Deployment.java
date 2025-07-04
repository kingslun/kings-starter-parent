package io.kings.framework.devops.kubernetes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * kubernetes deployment描述对象
 *
 * @author lun.wang
 * @date 2021-08-07
 * @since v1.0
 */
@Getter
@Setter
public class Deployment extends KubernetesObject {

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class DeploymentSpec implements Spec {

        @Getter
        @Setter
        @Accessors(fluent = true)
        public static final class Strategy {

            @Getter
            @Setter
            @ToString
            public static final class RollingUpdate {

                private Integer maxSurge;
                private Integer maxUnavailable;
            }

            private String type;
            private RollingUpdate rollingUpdate;
        }

        private Integer minReadySeconds;
        private Boolean paused;
        private Integer progressDeadlineSeconds;
        private Integer replicas;
        private Integer revisionHistoryLimit;
        private LabelSelector selector;
        private Strategy strategy;
        private PodTemplateSpec template;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class Status {

        @Getter
        @Setter
        @Accessors(fluent = true)
        public static final class Condition {

            private OffsetDateTime lastTransitionTime;

            private OffsetDateTime lastUpdateTime;

            private String message;

            private String reason;

            private String status;

            private String type;
        }

        private Integer availableReplicas;

        private Integer collisionCount;

        private List<Condition> conditions;

        private Long observedGeneration;

        private Integer readyReplicas;

        private Integer replicas;

        private Integer unavailableReplicas;

        private Integer updatedReplicas;
    }

    private Status status;
}
