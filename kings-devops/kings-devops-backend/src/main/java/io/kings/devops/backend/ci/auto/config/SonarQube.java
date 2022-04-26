package io.kings.devops.backend.ci.auto.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * sonar信息
 *
 * @author lun.wang
 * @date 2022/3/25 1:59 PM
 * @since v2.5
 */
@Setter
@Getter
@ToString
@Accessors(fluent = true)
public class SonarQube {

    /**
     * sonar服务地址 如：http://home.io:9000/
     */
    private String host;
    /**
     * 上述sonar服务的登录凭证 为40位长度的字符串 mvn sonar:sonar命令需要此参数
     */
    private String login;
}
