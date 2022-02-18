package io.kings.framework.devops.kubernetes.watch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * kubernetes pod事件描述对象
 *
 * @author your name
 * @date 2022/2/10 10:33 AM
 * @since v2.3
 */
@Getter
@Setter
@ToString
class EventPod {

    /**
     * *** 所属环境 *** not empty
     */
    private String env;
    /**
     * 实例名称
     */
    private String name;
    /**
     * 宿主机IP
     */
    private String hostIp;
    /**
     * 实例IP
     */
    private String podIp;
    /**
     * 实例开机时间
     */
    private String startTime;
    /**
     * 部署计划名称 deployment/rs name
     */
    private String deployment;
    /**
     * 所属分区 ns
     */
    private String namespace;
    /**
     * 节点名称 node name
     */
    private String nodeName;
    /**
     * 所属语言
     */
    private String language;
    /**
     * 实例状态
     */
    private Status status;

    /**
     * 实例重启次数
     */
    private int restartCount;
    /**
     * 容器 阶段状态
     */
    private String phase;

    /**
     * 实例状态
     */
    enum Status {
        /**
         * 创建
         */
        CREAT,
        /**
         * 停机
         */
        SHUTDOWN,
        /**
         * 变更
         */
        PENDING,
        /**
         * 变更
         */
        TERMINATING,
        /**
         * 正在运行
         */
        RUNNING,
        /**
         * 未知异常
         */
        UNKNOWN,
        /**
         * 删除
         */
        DELETE,
    }

    /**
     * 重写setStatus方法 返回自身
     *
     * @param status pod状态
     * @return self
     */
    public EventPod withStatus(Status status) {
        this.status = status;
        return this;
    }
}
