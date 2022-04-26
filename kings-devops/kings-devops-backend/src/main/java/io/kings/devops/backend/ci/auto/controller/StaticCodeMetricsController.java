package io.kings.devops.backend.ci.auto.controller;

import io.kings.devops.backend.ci.auto.Response;
import io.kings.devops.backend.ci.auto.openapi.StaticCodeMetricsApi;
import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryRequestVo;
import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryResponseVo;
import io.kings.devops.backend.ci.auto.sonarqube.StaticCodeMetricsComponent;
import io.kings.devops.backend.ci.auto.sonarqube.openapi.dto.MetricKeyResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 静态代码扫描任务restful控制器
 *
 * @author lun.wang
 * @date 2022/3/22 2:01 PM
 * @since v2.5
 */
@RestController
@AllArgsConstructor
public class StaticCodeMetricsController implements StaticCodeMetricsApi {

    private StaticCodeMetricsComponent staticCodeMetricsComponent;

    @Override
    public Response<MetricKeyResponseDto> metricKeys() {
        return Response.success(staticCodeMetricsComponent.metricKeys());
    }

    @Override
    public Response<StaticCodeMetricsQueryResponseVo> metrics(
        StaticCodeMetricsQueryRequestVo requestVo) {
        return Response.success(staticCodeMetricsComponent.metrics(requestVo));
    }
}
