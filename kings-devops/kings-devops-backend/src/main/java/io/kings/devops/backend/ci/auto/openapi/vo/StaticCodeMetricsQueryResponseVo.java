package io.kings.devops.backend.ci.auto.openapi.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 项目指标返回体
 *
 * @author lun.wang
 * @date 2022/3/30 4:43 PM
 * @since v2.5
 */
@Getter
@Setter
@ToString
public class StaticCodeMetricsQueryResponseVo implements Serializable {

    private int pageNumber;
    private int pageSize;
    private int totalPage;
    private long totalCount;
    private List<ProjectMetric> projectMetrics;

    @Getter
    @Setter
    public static class ProjectMetric implements Serializable {

        private String appName;
        private String branch;
        private String projectName;
        private transient List<Metric> metrics;
    }

    @Getter
    @Setter
    public static class Metric implements Serializable {

        private String key;
        private String chineseDescribe;
        private String value;
    }
}
