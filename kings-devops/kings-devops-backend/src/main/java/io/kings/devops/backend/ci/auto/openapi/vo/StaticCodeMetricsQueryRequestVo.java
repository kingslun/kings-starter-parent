package io.kings.devops.backend.ci.auto.openapi.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 项目指标查询参数
 *
 * @author lun.wang
 * @date 2022/3/30 4:43 PM
 * @since v2.5
 */
@Getter
@Setter
@ToString
public class StaticCodeMetricsQueryRequestVo implements Serializable {

    private String appName;
    private String interestedMetrics = METRICS_OF_INTEREST;
    private Integer pageNumber = 1;
    private Integer pageSize = 10;

    //默认感兴趣的指标 缺陷、漏洞、覆盖率(%)、行覆盖率(%)、阻断问题、严重问题、主要问题、单元测试数、安全热点、重复率(%)、可维护性、可靠性、安全性
    public static final String METRICS_OF_INTEREST = "bugs,vulnerabilities,coverage,line_coverage,blocker_violations,critical_violations,major_violations,tests,security_remediation_effort,duplicated_lines_density,sqale_rating,reliability_rating,security_rating";
}
