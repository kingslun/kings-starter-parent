package io.kings.devops.backend.ci.auto.sonarqube;

import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryRequestVo;
import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryResponseVo;
import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryResponseVo.ProjectMetric;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanDo;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanRepository;
import io.kings.devops.backend.ci.auto.sonarqube.openapi.dto.MetricKeyResponseDto;
import io.kings.devops.backend.ci.auto.sonarqube.openapi.dto.MetricResponseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author lun.wang
 * @date 2022/3/30 6:13 PM
 * @since v2.5
 */
@Component
@AllArgsConstructor
public class StaticCodeMetricsComponent {

    private final SonarApiFeignClient sonarApiFeignClient;
    private final JenkinsTaskSonarScanRepository sonarScanTaskComponent;

    //How to optimize? Get sonarqube in batches...
    public StaticCodeMetricsQueryResponseVo metrics(StaticCodeMetricsQueryRequestVo requestVo) {
        Page<JenkinsTaskSonarScanDo> page = sonarScanTaskComponent.findAll(requestVo);
        StaticCodeMetricsQueryResponseVo responseVo = new StaticCodeMetricsQueryResponseVo();
        responseVo.setPageNumber(requestVo.getPageNumber());
        responseVo.setPageSize(requestVo.getPageSize());
        responseVo.setTotalPage(page.getTotalPages());
        responseVo.setTotalCount(page.getTotalElements());
        if (page.hasContent()) {
            List<ProjectMetric> projectMetrics = new ArrayList<>(requestVo.getPageSize());
            page.getContent().forEach(i -> projectMetrics.add(projectMetric(
                sonarApiFeignClient.metrics(i.getProjectKey(), requestVo.getInterestedMetrics()),
                i)));
            responseVo.setProjectMetrics(projectMetrics);
        }
        return responseVo;
    }

    public MetricKeyResponseDto metricKeys() {
        sonarApiFeignClient.index();
        return null;
    }

    private ProjectMetric projectMetric(MetricResponseDto dto, JenkinsTaskSonarScanDo item) {
        ProjectMetric projectMetric = new ProjectMetric();
        MetricResponseDto.Component component = dto.getComponent();
        if (component != null) {
            projectMetric.setAppName(item.getAppName());
            projectMetric.setBranch(item.getBranch());
            projectMetric.setProjectName(component.getProjectName());
            List<MetricResponseDto.Metric> metrics = component.getMetrics();
            if (!CollectionUtils.isEmpty(metrics)) {
                List<StaticCodeMetricsQueryResponseVo.Metric> responseMetrics = new ArrayList<>(
                    metrics.size());
                metrics.forEach(i -> {
                    StaticCodeMetricsQueryResponseVo.Metric responseMetric = new StaticCodeMetricsQueryResponseVo.Metric();
                    String key = i.getKey();
                    responseMetric.setKey(key);
                    responseMetric.setValue(i.getValue());
                    responseMetric.setChineseDescribe(MetricDictionary.chinese(key));
                    responseMetrics.add(responseMetric);
                });
                projectMetric.setMetrics(responseMetrics);
            }
        }
        return projectMetric;
    }
}
