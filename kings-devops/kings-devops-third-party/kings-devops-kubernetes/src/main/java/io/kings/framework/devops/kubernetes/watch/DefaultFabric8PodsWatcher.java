package io.kings.framework.devops.kubernetes.watch;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;

/**
 * 基于Fabric8框架实现的对k8s集群下指定namespace节点下所有pod实例状态监控管理的注册中心
 * 能基于pod生命周期内各个阶段进行快速响应
 * feature:对pod宕机进行检测 发送钉钉通知
 * See <a href="http://code.aihuishou.com/fusion/ahs-nova/-/issues/4">shutdown listing</a> for a details of the feature
 *
 * @see K8sPodListener 通过对pod各阶段的状态变更抽离的监听接口 是对各阶段响应后的业务逻辑进行抽离和封装
 */
@Slf4j
public class DefaultFabric8PodsWatcher extends AbstractFabric8PodsWatcher {

    public DefaultFabric8PodsWatcher(KubernetesClient client, K8sPodListener listener) {
        super(client, listener);
    }

    public DefaultFabric8PodsWatcher(KubernetesClient client, K8sPodListener listener, ExecutorService service) {
        super(client, listener, service);
    }

    /**
     * 对K8s集群下所有的pods添加监听器
     * 支持指定namespace下的pods进行监听
     *
     * @param ns namespace不传默认监听所有
     * @author lun.wang
     * @date 2021/06/21 18:11
     * @see K8sPodListener
     * @since v1.1
     */
    @Override
    public boolean open(String ns) {
        if (this.openly()) {
            throw new K8sPodListener.Exception("Listening was already opened...");
        }
        PodWatcher watcher = new PodWatcher(listener, this.executors, () -> this.open(ns), this);
        try {
            Watch watch;
            if (StringUtils.hasText(ns)) {
                watch = this.client.pods().inNamespace(ns).watch(watcher);
            } else {
                watch = this.client.pods().inAnyNamespace().watch(watcher);
            }
            log.debug("AddPodListener to env:{} @ namespace:{} success", listener.env(), ns);
            super.opened();
            return this.watches.offer(watch);
        } catch (Exception e) {
            listener.onException(new K8sPodListener.Exception(e));
            log.warn("AddPodListener to env:{} @ namespace:{} failure", listener.env(), ns);
            return false;
        }
    }
}
