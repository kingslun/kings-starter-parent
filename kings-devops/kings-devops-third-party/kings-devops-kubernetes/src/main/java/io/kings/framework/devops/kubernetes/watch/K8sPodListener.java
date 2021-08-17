package io.kings.framework.devops.kubernetes.watch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * k8s容器pod实例监听器 监听实例扩容、缩容、更新事件
 * exp: 对单环境下指定namespace下的pods进行监听
 * env===>ns===>pod
 * </p>
 *
 * @author lun.wang
 * @date 2021/6/21 5:38 下午
 * @since 1.0
 */
public interface K8sPodListener extends NamingAware {
    /**
     * 环境信息提供者
     */
    String env();

    /**
     * K8s虚拟机节点实例信息描述对象
     * pod描述信息
     */
    @Getter
    @Setter
    @ToString
    class Pod {
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
        private PodStatus status;

        /**
         * 重写setStatus方法 返回自身
         *
         * @param status pod状态
         * @return self
         */
        public Pod withStatus(PodStatus status) {
            this.status = status;
            return this;
        }

        /**
         * 实例重启次数
         */
        private int restartCount;
        /**
         * 容器 阶段状态
         */
        private String phase;
    }

    /**
     * 实例状态
     */
    enum PodStatus {
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

    class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }

        public Exception(Throwable cause) {
            super(cause);
        }
    }

    /**
     * ns下有新建pod
     *
     * @param pod 创建的Pod
     */
    default void onPodCreating(Pod pod) {

    }

    /**
     * ns下有pod关机
     *
     * @param pod 销毁的Pod
     */
    default void onPodShutdown(Pod pod) {

    }

    /**
     * ns下有pod移除
     *
     * @param pod 移除的Pod
     */
    default void onPodDelete(Pod pod) {

    }

    /**
     * ns下有pod发生异常
     *
     * @param pod 异常的Pod
     */
    default void onPodUnKnown(Pod pod) {

    }

    /**
     * pod运行状态 这个阶段可能会多次调用 因为watcher首次初始化或者容器准备阶段成功之后会进入运行态
     *
     * @param pod 运行的Pod
     */
    default void onPodRunning(Pod pod) {

    }

    /**
     * pod准备阶段 这个阶段可能会频繁调用 因为节点初始化和重启、停机等多个阶段都会有对应准备流程 因此此事件是共享的
     * 共享PENDING和TERMINATING两种状态
     *
     * @param pod 正在准备的pod
     * @see PodStatus#PENDING,PodStatus#TERMINATING
     */
    default void onPodPending(Pod pod) {

    }

    /**
     * 监听关闭时触发
     *
     * @param e error
     */
    default void onException(Exception e) {

    }

    /**
     * 监听通道关闭时触发
     *
     * @param e error
     */
    default void onClose(Exception e) {

    }

    /**
     * 等待状态
     */
    String PENDING = "Pending";
    /**
     * 运行状态
     */
    String RUNNING = "Running";
    /**
     * k8s pod未完成状态
     */
    String FALSE = "False";
}
