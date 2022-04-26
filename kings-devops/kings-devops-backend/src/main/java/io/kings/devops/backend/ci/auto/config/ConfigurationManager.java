package io.kings.devops.backend.ci.auto.config;

import com.offbytwo.jenkins.JenkinsServer;
import org.springframework.lang.NonNull;

/**
 * 配置信息提供者
 *
 * @author lun.wang
 * @date 2022/3/25 1:58 PM
 * @since v2.5
 */
public interface ConfigurationManager {

    /**
     * 根据服务名称获取对应的仓库配置信息
     *
     * @param appName 服务名
     * @return Git
     */
    @NonNull
    Git git(String appName);

    /**
     * 根据环境信息获取sonar服务信息
     *
     * @param env 环境
     * @return SonarQube
     */
    @NonNull
    SonarQube sonarQube(String env);

    //具有本地缓存能力的Jenkins服务查询入口
    @NonNull
    JenkinsServer jenkinsServer(String env);
}
