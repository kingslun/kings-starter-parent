package io.kings.framework.devops.openapi.model;

import io.kings.framework.core.Enumerable;
import io.kings.framework.devops.openapi.exception.JenkinsProjectException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author lun.wang
 * @date 2021/7/30 7:20 下午
 * @since v1.0
 */
@Getter
@Setter
@ToString
@Schema(name = "Jenkins项目信息描述对象")
public class ProjectVo implements Serializable {

    static class JenkinsProjectTypeNotFoundException extends JenkinsProjectException {

        JenkinsProjectTypeNotFoundException() {
            super(Arrays.toString(Type.values()) + " defined");
        }
    }

    /**
     * jenkins工程类型 目前支持pipeline
     */
    enum Type implements Enumerable<Integer, String> {
        PIPELINE(1, "流水线工程");
        @Getter
        private final Integer code;
        @Getter
        private final String desc;

        /**
         * find first type in Type values 应：有且仅有唯一的code 否则异常或抛错
         *
         * @param code 应用类型代码 供front与backend匹配使用
         * @return Type of project
         * @throws JenkinsProjectException none
         */
        static Type of(int code) throws JenkinsProjectException {
            return Arrays.stream(Type.values()).filter(i -> i.code == code).findFirst()
                    .orElseThrow(JenkinsProjectTypeNotFoundException::new);
        }

        Type(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Schema(name = "应用名称")
    private String name;

    @Schema(name = "仓库地址")
    private Type type;

    public void setType(int code) throws JenkinsProjectException {
        this.type = Type.of(code);
    }

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
