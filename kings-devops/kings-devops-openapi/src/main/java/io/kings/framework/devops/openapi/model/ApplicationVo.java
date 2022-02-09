package io.kings.framework.devops.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lun.wang
 * @date 2021/7/30 7:20 下午
 * @since v1.0
 */
@Getter
@Setter
@ToString
@Schema(name = "应用信息描述对象")
public class ApplicationVo implements Serializable {

    @Schema(name = "应用名称")
    private String name;

    @Schema(name = "仓库地址")
    private String git;

    @Schema(name = "构建目录")
    private String projectPath;

    @Schema(name = "应用描述")
    private String describe;

    @Schema(name = "技术栈")
    private String techStack;

    @Schema(name = "对外服务名称")
    private String contextPath;

    /**
     * 默认采用spring-boot-actuator暴露的接口
     */
    @Schema(name = "健康检查接口")
    private String healthCheckPath;
}
