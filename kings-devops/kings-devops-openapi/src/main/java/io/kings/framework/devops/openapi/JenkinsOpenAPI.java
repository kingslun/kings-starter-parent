package io.kings.framework.devops.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 调用Jenkins CI接口
 *
 * @author lun.wang
 * @date 2021/8/8 2:07 下午
 * @since v1.0
 */
@Tag(name = "jenkins ci OpenAPI")
@RequestMapping("/v1/jenkins/")
public interface JenkinsOpenAPI {

    @Operation(description = "创建Jenkins构建工程")
    @PostMapping("/project/create")
    void createProject();

    @Operation(description = "删除Jenkins构建工程")
    @DeleteMapping("/project/delete/{id}")
    void deleteProject(@PathVariable String id);

    @Operation(description = "修改Jenkins构建工程")
    @PatchMapping("/project/patch")
    void patchProject();

    @Operation(description = "获取Jenkins构建工程")
    @GetMapping("/project/get/{id}")
    void getProject(@PathVariable String id);
}
