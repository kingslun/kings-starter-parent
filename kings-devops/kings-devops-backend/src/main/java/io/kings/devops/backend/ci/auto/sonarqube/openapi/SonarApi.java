package io.kings.devops.backend.ci.auto.sonarqube.openapi;

import io.kings.devops.backend.ci.auto.sonarqube.openapi.dto.MetricKeyResponseDto;
import io.kings.devops.backend.ci.auto.sonarqube.openapi.dto.MetricResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 对接sonar
 *
 * @author lun.wang
 * @date 2022/3/30 5:05 PM
 * @since v2.5
 */
public interface SonarApi {

    //默认查询所有指标(500项) sonar7社区版目前一共145项指标
    @GetMapping("/api/metrics/search?ps=500")
    MetricKeyResponseDto metricKeys();


    /**
     * 查询项目指标
     *
     * @param projectKey 项目key
     * @param metricKeys 感兴趣的指标key
     * @return MetricResponseDto
     */
    @GetMapping("/api/measures/component")
    MetricResponseDto metrics(@RequestParam("componentKey") String projectKey,
                              @RequestParam("metricKeys") String metricKeys);
}
