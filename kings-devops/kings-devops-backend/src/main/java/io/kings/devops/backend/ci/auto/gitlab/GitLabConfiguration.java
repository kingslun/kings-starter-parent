package io.kings.devops.backend.ci.auto.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.ProjectApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试阶段使用的gitlab配置
 * <code>GitLabApi gitLabApi = new GitLabApi("http://your.gitlab.server.com",
 * "YOUR_PERSONAL_ACCESS_TOKEN");</code>
 *
 * @author lun.wang
 * @date 2022/3/25 7:02 PM
 * @since v2.5
 */
@Configuration
public class GitLabConfiguration {

    @Value("${gitlab.host}")
    private String host;
    @Value("${gitlab.access-token}")
    private String accessToken;

    @Bean
    ProjectApi projectApi() {
        return new ProjectApi(new GitLabApi(host, accessToken));
    }
}
