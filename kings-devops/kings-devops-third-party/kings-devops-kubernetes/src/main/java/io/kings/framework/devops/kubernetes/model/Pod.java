package io.kings.framework.devops.kubernetes.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * kubernetes pod描述对象
 *
 * @author lun.wang
 * @date 2021/8/7 12:51 下午
 * @since v1.0
 */
@Setter
@Getter
@ToString
public class Pod {

    /**
     * 实例名称
     */
    private String podName;
    /**
     * 镜像版本号
     */
    private String version;
    /**
     * 实例IP
     */
    private String podIp;
    /**
     * 主机IP
     */
    private String hostIp;
    /**
     * 实例所在节点名称 kubernetes节点
     */
    private String nodeName;
    /**
     * 实例状态
     */
    private String phase;
    /**
     * 上次退出原因
     */
    private String lastTerminatingPhase;
    /**
     * 容器列表
     */
    private List<Container> containers;
    /**
     * 创建时间
     */
    private long creationTime;
    /**
     * 重启次数
     */
    private Integer restartCount;

    /**
     * 实例应用镜像容器描述对象
     */
    @Getter
    @Setter
    @ToString
    public static class Container {

        /**
         * 容器名称
         */
        private String containerName;

        /**
         * 镜像名称
         */
        private String image;

        /**
         * 镜像拉取策略
         */
        private String imagePullPolicy;

        /**
         * 工作目录
         */
        private String workingDir;
    }
}
