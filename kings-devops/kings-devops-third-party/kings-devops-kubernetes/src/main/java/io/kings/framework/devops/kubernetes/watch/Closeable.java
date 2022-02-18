package io.kings.framework.devops.kubernetes.watch;

/**
 * 通道管理
 *
 * @author lun.wang
 * @date 2021/6/24 6:02 下午
 * @since v1.0
 */
interface Closeable extends AutoCloseable {

    void close();
}
