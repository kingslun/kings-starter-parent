package io.kings.framework.devops.kubernetes.fabric8.watch;

/**
 * 可重启的状态
 *
 * @author lun.wang
 * @date 2021/6/24 6:02 下午
 * @since v1.0
 */
@FunctionalInterface
interface Retryable {

    /**
     * 是否在自动中断之后重连（k8s watch机制会存在自动中断的风险 需要主动重试） 可通过api对其进行设置 应用场景为主从模式下切换导致的主动关闭从节点监听
     *
     * @return true/false
     */
    boolean retryable();

    /**
     * change the retry switch
     *
     * @see this#retryable
     */
    default void turnoff() {

    }
}
