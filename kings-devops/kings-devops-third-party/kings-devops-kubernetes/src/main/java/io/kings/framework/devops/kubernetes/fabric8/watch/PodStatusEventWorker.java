package io.kings.framework.devops.kubernetes.fabric8.watch;

import org.springframework.util.Assert;

/**
 * <p>
 * pod节点事件处理工作线程
 * </p>
 *
 * @author lun.wang
 * @date 2021/6/23 3:52 下午
 * @since v1.1
 */
class PodStatusEventWorker implements Runnable {

    private final EventPod pod;
    private final K8sPodListener listener;

    public PodStatusEventWorker(K8sPodListener listener, EventPod pod) {
        Assert.notNull(listener, "Apply kubernetes listener is null");
        Assert.notNull(pod, "Apply kubernetes listener arguments pod is null");
        this.pod = pod;
        this.listener = listener;
    }

    @Override
    public void run() {
        //统一设置环境信息
        this.pod.setEnv(this.listener.env());
        switch (pod.getStatus()) {
            case CREAT:
                this.listener.onPodCreating(pod);
                break;
            case DELETE:
                this.listener.onPodDelete(pod);
                break;
            //TERMINATING PENDING状态共享
            case TERMINATING:
            case PENDING:
                this.listener.onPodPending(pod);
                break;
            case RUNNING:
                this.listener.onPodRunning(pod);
                break;
            case UNKNOWN:
                this.listener.onPodUnKnown(pod);
                break;
            case SHUTDOWN:
                this.listener.onPodShutdown(pod);
                break;
            default:
                throw new K8sPodListener.Exception("Illegal status exception @PodStatus");
        }
    }
}
