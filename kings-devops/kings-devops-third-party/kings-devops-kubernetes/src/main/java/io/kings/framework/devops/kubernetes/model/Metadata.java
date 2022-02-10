package io.kings.framework.devops.kubernetes.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 元信息
 *
 * @author lun.wang
 * @date 2022/2/10 4:18 PM
 * @since v2.3
 */
@Getter
@Setter
@Accessors(fluent = true)
public class Metadata {

    private Map<String, String> annotations;

    private String clusterName;

    private OffsetDateTime creationTimestamp;

    private Long deletionGracePeriodSeconds;

    private OffsetDateTime deletionTimestamp;

    private List<String> finalizers;

    private String generateName;

    private Long generation;

    private Map<String, String> labels;

    private String name;

    private String namespace;

    private String resourceVersion;

    private String selfLink;

    private String uid;
}
