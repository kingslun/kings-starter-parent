package io.kings.devops.backend.ci.auto.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * git信息
 *
 * @author lun.wang
 * @date 2022/3/25 1:59 PM
 * @since v2.5
 */
@Setter
@Getter
@ToString
@Accessors(fluent = true)
public class Git {

    /**
     * 项目仓库地址
     */
    private String url;
    /**
     * hook api用到的参数 格式为namespace/projectName eg: lun.wang/cmdb
     */
    private String projectPath;
    /**
     * Jenkins配置中心管理的秘钥ID 可根据此ID从Jenkins获取上述git仓库的账户信息
     */
    private String credentialsId;

    //也可传入账户密码 但Jenkins任务似乎不支持这种方式
    private String username;
    private String password;
}
