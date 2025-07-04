package io.kings.devops.backend.ci.auto.config;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 对接外部数据源获取对应信息的解耦实现
 * <br> Connect with the external environment to obtain various basic data
 *
 * @author lun.wang
 * @date 2022/3/25 2:13 PM
 * @since v2.5
 */
@Component
class DefaultConfigurationManager implements ConfigurationManager {

    //+++++++++++++++++++++测试阶段写死的本地环境 将来会移除+++++++++++++++++++++
    @Value("${jenkins.host}")
    private String jenkinsHost;
    @Value("${jenkins.username}")
    private String jenkinsUsername;
    @Value("${jenkins.password}")
    private String jenkinsPassword;
    @Value("${sonarqube.host}")
    private String sonarqubeHost;
    @Value("${sonarqube.login}")
    private String sonarqubeLogin;
    @Value("${git.url}")
    private String gitUrl;
    @Value("${git.credentials_id}")
    private String gitCredentialsId;
    //---------------------测试阶段写死的本地环境 将来会移除---------------------

    /**
     * 获取项目相对路径 for gitlab api
     *
     * @param url 目标项目的git地址（兼容ssh考虑兼容https?）
     * @return namespace/projectName
     */
    public String projectPath(String url) {
        Assert.hasText(url, "git host is empty");
        return url.substring(url.indexOf('/', url.indexOf("git@")) + 1, url.indexOf(".git"));
    }

    //对从外部获取的git信息做一层缓存方便下一次快速获取
    private final Map<String, Git> gitMap = new HashMap<>(64);
    //对Jenkins和sonar服务配置做缓存 key: env
    private final Map<String, BasicEnvironment> basicEnvironmentMap = new HashMap<>(8);

    //根据服务名称从服务表获取服务仓库信息
    @Override
    public Git git(String appName) {
        return gitMap.computeIfAbsent(appName,
                app -> new Git().url(gitUrl).projectPath(projectPath(gitUrl))
                        .credentialsId(gitCredentialsId));
    }

    @Override
    public SonarQube sonarQube(String env) {
        return basicEnvironmentMap.computeIfAbsent(env, this::fromEnv).sonarQube();
    }

    //测试阶段使用本地Jenkins
    private JenkinsServer testJenkinsServer() {
        HttpClientBuilder config = HttpClientBuilder.create().setDefaultRequestConfig(
                RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                        .setSocketTimeout(5000).build());
        JenkinsHttpClient client = new JenkinsHttpClient(URI.create(jenkinsHost), config,
                jenkinsUsername, jenkinsPassword);
        return new JenkinsServer(client);
    }

    //根据环境获取jenkins集群信息
    @Override
    public JenkinsServer jenkinsServer(String env) {
        return basicEnvironmentMap.computeIfAbsent(env, this::fromEnv).jenkinsServer();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static class BasicEnvironment {

        private JenkinsServer jenkinsServer;
        private SonarQube sonarQube;
    }

    //根据环境获取基础数据 LIST2
    private BasicEnvironment fromEnv(String env) {
        //测试阶段仅返回本地环境
        return new BasicEnvironment().jenkinsServer(testJenkinsServer())
                .sonarQube(new SonarQube().host(sonarqubeHost).login(sonarqubeLogin));
    }
}
