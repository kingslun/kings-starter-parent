package io.kings.devops.backend.ci.auto.controller;

import io.kings.devops.backend.ci.auto.Response;
import io.kings.devops.backend.ci.auto.jenkins.JenkinsTask4SonarScanComponent;
import io.kings.devops.backend.ci.auto.openapi.JenkinsTaskApi;
import io.kings.devops.backend.ci.auto.openapi.vo.CreateSonarScanTaskRequestVo;
import io.kings.devops.backend.ci.auto.openapi.vo.CreateSonarScanTaskResponseVo;
import javax.validation.Valid;
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
public class JenkinsTaskController implements JenkinsTaskApi {

    private final JenkinsTask4SonarScanComponent sonarScanComponent;

    @Override
    public Response<CreateSonarScanTaskResponseVo> createStaticScanTask(
        @Valid CreateSonarScanTaskRequestVo requestVo) {
        return Response.success(sonarScanComponent.createStaticScanTask(requestVo));
    }

    @Override
    public Response<Void> deleteStaticScanTask(String projectKey) {
        sonarScanComponent.deleteStaticScanTask(projectKey);
        return Response.success(null);
    }

    @Override
    public Response<String> latestStaticScanTaskConsoleLog(String projectKey) {
        return Response.success(sonarScanComponent.latestStaticScanTaskConsoleLog(projectKey));
    }
}
