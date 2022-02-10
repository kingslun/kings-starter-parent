package io.kings.framework.devops.kubernetes.fabric8.watch;

import io.kings.framework.core.bean.BeanNameDefinition;

/**
 * <p>
 * k8s容器pod实例监听器 监听实例扩容、缩容、更新事件 exp: 对单环境下指定namespace下的pods进行监听 env===>ns===>pod
 * </p>
 *
 * @author lun.wang
 * @date 2021/6/21 5:38 下午
 * @since 1.0
 */
public interface K8sPodListener extends BeanNameDefinition {

    /**
     * 环境信息提供者
     */
    String env();

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
    default void onPodCreating(EventPod pod) {
    }

    /**
     * ns下有pod关机
     *
     * @param pod 销毁的Pod
     */
    default void onPodShutdown(EventPod pod) {

    }

    /**
     * ns下有pod移除
     *
     * @param pod 移除的Pod
     */
    default void onPodDelete(EventPod pod) {

    }

    /**
     * ns下有pod发生异常
     *
     * @param pod 异常的Pod
     */
    default void onPodUnKnown(EventPod pod) {

    }

    /**
     * pod运行状态 这个阶段可能会多次调用 因为watcher首次初始化或者容器准备阶段成功之后会进入运行态
     *
     * @param pod 运行的Pod
     */
    default void onPodRunning(EventPod pod) {

    }

    /**
     * pod准备阶段 这个阶段可能会频繁调用 因为节点初始化和重启、停机等多个阶段都会有对应准备流程 因此此事件是共享的 共享PENDING和TERMINATING两种状态
     *
     * @param pod 正在准备的pod
     * @see EventPod.Status#PENDING,EventPod.Status#TERMINATING
     */
    default void onPodPending(EventPod pod) {

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
