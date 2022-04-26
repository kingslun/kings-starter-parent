package io.kings.devops.backend.ci.auto.openapi;

import io.kings.devops.backend.ci.auto.Response;
import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryRequestVo;
import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryResponseVo;
import io.kings.devops.backend.ci.auto.sonarqube.openapi.dto.MetricKeyResponseDto;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 项目静态代码指标接口
 *
 * @author lun.wang
 * @date 2022/3/30 4:34 PM
 * @since v2.5
 */
@RequestMapping("/api/v1/project")
public interface StaticCodeMetricsApi {

    @GetMapping("/metrics/key/list")
    Response<MetricKeyResponseDto> metricKeys();

    @GetMapping("/metrics/list")
    Response<StaticCodeMetricsQueryResponseVo> metrics(
        @Valid StaticCodeMetricsQueryRequestVo requestVo);
}
