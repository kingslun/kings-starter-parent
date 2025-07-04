package io.kings.framework.devops.kubernetes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * pod描述对象
 *
 * @author lun.wang
 * @date 2022/2/10 6:02 PM
 * @since v2.3
 */
@Getter
@Setter
@Accessors(fluent = true)
public class PodSpec {

    private Long activeDeadlineSeconds;
    private Boolean automountServiceAccountToken;
    private String dnsPolicy;
    private Boolean enableServiceLinks;
    private Boolean hostIPC;
    private Boolean hostNetwork;
    private Boolean hostPID;
    private String hostname;
    private String nodeName;
    private Map<String, String> nodeSelector;
    private String preemptionPolicy;
    private Integer priority;
    private String priorityClassName;
    private String restartPolicy;
    private String runtimeClassName;
    private String schedulerName;
    private String serviceAccount;
    private String serviceAccountName;
    private Boolean setHostnameAsFQDN;
    private Boolean shareProcessNamespace;
    private String subdomain;
    private Long terminationGracePeriodSeconds;
    private List<Toleration> tolerations;
    private List<TopologySpreadConstraint> topologySpreadConstraints;
    private List<Volume> volumes;

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class Volume {

        @Getter
        @Setter
        @Accessors(fluent = true)
        public static final class EmptyDir {

            private String medium;
        }

        @Getter
        @Setter
        @Accessors(fluent = true)
        public static final class HostPath {

            private String path;
            private String type;
        }

        @Getter
        @Setter
        @Accessors(fluent = true)
        public static final class Secret {

            @Getter
            @Setter
            @Accessors(fluent = true)
            public static final class Item {

                private String key;
                private Integer mode;
                private String path;
            }

            private Integer defaultMode;
            private Boolean optional;
            private String secretName;
            private List<Item> items;
        }

        private EmptyDir emptyDir;
        private String name;
        private HostPath hostPath;
        private Secret secret;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class TopologySpreadConstraint {

        private LabelSelector labelSelector;
        private Integer maxSkew;
        private String topologyKey;
        private String whenUnsatisfiable;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class Toleration {

        private String effect;

        private String key;

        private String operator;

        private Long tolerationSeconds;

        private String value;
    }
}
