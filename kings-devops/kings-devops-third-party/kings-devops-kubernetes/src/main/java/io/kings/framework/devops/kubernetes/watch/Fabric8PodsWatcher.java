package io.kings.framework.devops.kubernetes.watch;

import org.springframework.beans.factory.DisposableBean;

public interface Fabric8PodsWatcher extends DisposableBean,
        Retryable, Closeable {

    /**
     * 对指定namespace下的pods开启监听
     *
     * @param ns namespace
     * @return true/false
     * @author lun.wang
     * @date 2021/06/21 18:11
     * @since v1.1
     */
    boolean open(String ns);

    /**
     * 开启状态
     */
    boolean openly();
}
