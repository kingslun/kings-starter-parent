package io.kings.devops.backend.ci.auto.openapi;

import io.kings.devops.backend.ci.auto.Response;
import io.kings.devops.backend.ci.auto.openapi.vo.CreateSonarScanTaskRequestVo;
import io.kings.devops.backend.ci.auto.openapi.vo.CreateSonarScanTaskResponseVo;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * jenkins任务API
 *
 * @author lun.wang
 * @date 2022/3/22 2:07 PM
 * @since v2.5
 */
@RequestMapping("/api/v1/jenkins")
public interface JenkinsTaskApi {

    @PostMapping("/task/static-scan")
    Response<CreateSonarScanTaskResponseVo> createStaticScanTask(
            @Valid @RequestBody CreateSonarScanTaskRequestVo requestVo);

    @DeleteMapping("/task/static-scan/{projectKey}")
    Response<Void> deleteStaticScanTask(@PathVariable String projectKey);

    //查询临近的日志
    @GetMapping("/task/static-scan/{projectKey}/console")
    Response<String> latestStaticScanTaskConsoleLog(@PathVariable String projectKey);
}
