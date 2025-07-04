package io.kings.devops.backend.ci.auto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * 项目名称生成器 生成jenkins name、sonarqube name
 *
 * @author lun.wang
 * @date 2022/3/25 2:55 PM
 * @since v2.5
 */
@FunctionalInterface
public interface ProjectKeyGenerator {

    /**
     * 考虑到不同环境下同一应用的不同分支唯一
     *
     * @param env     环境
     * @param appName 应用名称
     * @param branch  分支
     * @return primary key
     */
    String projectKey(String env, String appName, String branch);

    ProjectKeyGenerator DEFAULT = (env, appName, branch) -> new ProjectKey(env, appName,
            branch).toString();

    @Getter
    @AllArgsConstructor
    class ProjectKey {

        private static final String PROJECT_DELIMITER = "_";

        @Nullable
        private String env;
        @NonNull
        private String appName;
        @NonNull
        private String branch;

        @Override
        public String toString() {
            return (StringUtils.hasText(env) ? env + PROJECT_DELIMITER : "") + appName.replace("-",
                    PROJECT_DELIMITER) + PROJECT_DELIMITER + branch;
        }
    }
}
