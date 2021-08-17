package io.kings.framework.component.zookeeper;

/**
 * 异常监听 执行报错的时候回调函数
 *
 * @author lun.wang
 * @date 2020/4/24 3:30 下午
 * @since v2.7.2
 */
@FunctionalInterface
public interface ZookeeperAsyncErrorListener {
    /**
     * failed
     *
     * @param msg       message?
     * @param throwable fail cause
     */
    void onFail(Throwable throwable, String msg);
}
