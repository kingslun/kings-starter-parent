package io.kings.framework.devops.kubernetes.watch;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class AbstractFabric8PodsWatcher implements Fabric8PodsWatcher {

    /**
     * 重试连接k8s的开关 默认不重连 -只有在connect 成功后断连才允许reconnect
     */
    private volatile boolean retryable = false;
    /**
     * 是否开启状态 true is already open
     */
    private volatile boolean openly = false;
    /**
     * 具体操作k8s集群的客户端连接工具 by Fabric8框架
     */
    protected final KubernetesClient client;
    /**
     * 工作线程池
     */
    protected final ExecutorService executors;
    /**
     * 默认的工作线程池
     */
    private static final ExecutorService DEFAULT;
    private static final AtomicInteger THREADS;
    /**
     * 监听器
     */
    protected final K8sPodListener listener;

    static {
        THREADS = new AtomicInteger(1);
        //获取1/4的CPU执行-可根据实际工作量来进行动态调整
        DEFAULT = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4,
            r -> {
                Thread worker = new Thread(r, "K8sPodStatusWatcher" + THREADS.getAndIncrement());
                worker.setUncaughtExceptionHandler(
                    (t, e) -> log.warn("Pod watcher Thread name:{} report:{}", t.getName(),
                        e.getMessage()));
                return worker;
            });
    }

    protected AbstractFabric8PodsWatcher(KubernetesClient client,
        K8sPodListener listener) {
        this(client, listener, DEFAULT);
    }

    protected AbstractFabric8PodsWatcher(KubernetesClient client,
        K8sPodListener listener,
        ExecutorService service) {
        Assert.notNull(client, "Add pods listener without KubernetesClient");
        Assert.notNull(listener, "Listen on pods without an owner");
        Assert.notNull(service, "Please configure an executors threads pool");
        this.client = client;
        this.listener = listener;
        this.executors = service;
    }

    @Override
    public final boolean retryable() {
        return this.retryable;
    }

    /**
     * 最多创建5个监听
     */
    protected final Queue<Watch> watches = new ArrayBlockingQueue<>(5);

    @Override
    public void close() {
        if (this.openly()) {
            Watch watch;
            while ((watch = watches.poll()) != null) {
                watch.close();
            }
            this.turnoff();
        } else {
            throw new K8sPodListener.Exception("Already closed...");
        }
    }

    /**
     * 跟随IOC容器生命周期终止而关闭
     */
    @Override
    public final void destroy() {
        this.turnoff();
        this.client.close();
    }

    @Override
    public boolean openly() {
        return this.openly;
    }

    protected void opened() {
        this.openly = true;
        this.retryable = true;
    }

    @Override
    public void turnoff() {
        this.retryable = false;
        this.openly = false;
    }

    /**
     * pod状态判断 conditions 分别有四个状态 Initialized Ready ContainersReady PodScheduled
     * containerStatuses分别有两种状态 started ready 如果一个为false则表示pod并非running状态
     */
    private static final Predicate<PodStatus> READY = status -> {
        List<PodCondition> conditions = status.getConditions();
        List<ContainerStatus> statuses = status.getContainerStatuses();
        for (PodCondition condition : conditions) {
            if (Objects.equals(K8sPodListener.FALSE, condition.getStatus())) {
                return false;
            }
        }
        for (ContainerStatus status1 : statuses) {
            if (Objects.equals(Boolean.FALSE, status1.getReady()) ||
                Objects.equals(Boolean.FALSE, status1.getStarted())) {
                return false;
            }
        }
        return true;
    };

    /*
     * shutdown状态
     * 因为以下判断条件可能跟随三方组件API调整或切换导致变动 抽离出来是为了迎合上述变动而自身的最小变动
     * 1.conditions 四个状态 Initialized:true Ready:false ContainersReady:false PodScheduled:true
     * 2.containerStatuses started:false ready:false
     * 3.containerStatuses.state 有terminated属性
     */
    private static final Predicate<PodStatus> SHUTDOWN_ = status -> {
        //依据SHUTDOWN的规则他的READY为false
        if (READY.test(status)) {
            return false;
        }
        List<ContainerStatus> statuses = status.getContainerStatuses();
        for (ContainerStatus status1 : statuses) {
            if (status1.getState().getTerminated() != null) {
                return true;
            }
        }
        return false;
    };

    /**
     * pod是否Terminating状态 目前fabric8检测的k8s pod Terminating共有4种状态
     * <p>
     * 其中三种为shutdown之初到彻底delete之间 - 分三阶段 running->ContainersNotReady 一种为delete状态 不在此处验证
     * </p>
     * 他们的特点是metadata信息包含deletionTimestamp时间凭证 deletionGracePeriodSeconds：为具体优雅停机最大等待时间（猜测）这个值在会在最后一次置空
     * 因此不能拿来衡量
     */
    private static final Predicate<String> TERMINATING_ = StringUtils::hasText;

    /**
     * 区分是watcher初始化还是真正的pod新建 真正的新建条件 1.spec未分配node name 2.phase为Pending状态 3.不包含状态信息和容器状态信息
     * 4.不包含如果容器IP信息 5.不包含开启时间信息
     */
    private static final BiPredicate<EventPod, PodStatus> ADD_RUNNING =
        (pod, status) ->
            StringUtils.hasText(pod.getNodeName()) ||
                !Objects.equals(K8sPodListener.PENDING, pod.getPhase()) ||
                StringUtils.hasText(pod.getStartTime()) ||
                StringUtils.hasText(pod.getHostIp()) || StringUtils.hasText(pod.getPodIp()) ||
                !CollectionUtils.isEmpty(status.getConditions()) ||
                !CollectionUtils.isEmpty(status.getContainerStatuses());

    /**
     * K8s pod监听器 负责衔接fabric8框架和K8sPodListener的桥梁
     *
     * @see K8sPodListener, Watcher , Pod
     */
    @Slf4j
    static class PodWatcher implements Watcher<Pod> {

        private final K8sPodListener listener;
        private final ExecutorService executorService;
        /**
         * <a href="http://jira.aihuishou.com/browse/ZEUS-43">处理监听通道关闭的异常场景</a>
         */
        private final Callable<Boolean> retryHook;
        private final Retryable retryable;

        protected PodWatcher(K8sPodListener listener, ExecutorService executorService,
            Callable<Boolean> retryHook,
            Retryable retryable) {
            this.listener = listener;
            this.executorService = executorService;
            this.retryHook = retryHook;
            this.retryable = retryable;
        }

        private void submit(EventPod pod) {
            this.executorService.submit(new PodStatusEventWorker(this.listener, pod));
        }

        private EventPod convert(Pod pod) {
            final ObjectMeta metadata = pod.getMetadata();
            final PodStatus status = pod.getStatus();
            EventPod bak = new EventPod();
            bak.setName(metadata.getName());
            bak.setDeployment(metadata.getLabels().get("app"));
            bak.setLanguage(metadata.getLabels().get("language"));
            bak.setNamespace(metadata.getNamespace());
            //真正的新建是还没有分配node name的
            bak.setNodeName(pod.getSpec().getNodeName());
            bak.setHostIp(status.getHostIP());
            bak.setPodIp(status.getPodIP());
            bak.setStartTime(status.getStartTime());
            bak.setPhase(status.getPhase());
            if (!CollectionUtils.isEmpty(status.getContainerStatuses())) {
                bak.setRestartCount(status.getContainerStatuses().get(0).getRestartCount());
            }
            return bak;
        }

        @Override
        public void eventReceived(Action action, Pod pod) {
            final PodStatus status = pod.getStatus();
            EventPod bak = convert(pod);
            switch (action) {
                case MODIFIED:
                    /*
                     * 区分是pod初始化后的各个准备阶段还是重启的各阶段还是删除阶段的状态变更
                     * 此阶段会存在复杂判断需要保证严谨性
                     * 1.phase一定是Pending状态的一定是还在初始化阶段
                     */
                    if (Objects.equals(K8sPodListener.PENDING, bak.getPhase())) {
                        this.submit(bak.withStatus(EventPod.Status.PENDING));
                        break;
                    }
                    //2.Running状态的会有很多场景 Pending完成及shutdown/Terminating阶段都是Running
                    if (!Objects.equals(K8sPodListener.RUNNING, bak.getPhase())) {
                        this.submit(bak.withStatus(EventPod.Status.UNKNOWN));
                        break;
                    }
                    final List<PodCondition> conditions = status.getConditions();
                    final List<ContainerStatus> containerStatuses = status.getContainerStatuses();
                    if (CollectionUtils.isEmpty(conditions) || CollectionUtils.isEmpty(
                        containerStatuses)) {
                        //正常非pending状态不会至此
                        this.submit(bak.withStatus(EventPod.Status.UNKNOWN));
                        break;
                    }
                    if (SHUTDOWN_.test(status)) {
                        //吧shutdown摘出来
                        this.submit(bak.withStatus(EventPod.Status.SHUTDOWN));
                    } else if (TERMINATING_.test(pod.getMetadata().getDeletionTimestamp())) {
                        //吧Terminating摘出来
                        this.submit(bak.withStatus(EventPod.Status.TERMINATING));
                    } else {
                        this.submit(bak.withStatus(EventPod.Status.RUNNING));
                    }
                    break;
                case DELETED: //在删除deployment时触发 流程是先优雅停机所有节点,期间会触发多次MODIFIED 最终成功之后才会触发此入口
                    this.submit(bak.withStatus(EventPod.Status.DELETE));
                    break;
                case ADDED:
                    if (ADD_RUNNING.test(bak, status)) {
                        this.submit(bak.withStatus(EventPod.Status.RUNNING));
                    } else {
                        this.submit(bak.withStatus(EventPod.Status.CREAT));
                    }
                    break;
                case ERROR:
                    //目前未遇到过的状态 可能是pod异常时触发
                    this.submit(bak.withStatus(EventPod.Status.UNKNOWN));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override
        public void onClose(WatcherException e) {
            listener.onClose(new K8sPodListener.Exception(e));
            log.debug("Checked channel closed of the kubernetes container pods listener!");
            //对关闭的通道尝试重新建立
            for (int i = 1; retryable.retryable(); i++) {
                try {
                    log.debug("Retry to connect to k8s node...");
                    Future<Boolean> result = this.executorService.submit(this.retryHook);
                    if (Boolean.TRUE.equals(result.get())) {
                        log.debug(
                            "Retry to add the pods listener success to the kubernetes container!");
                        break;
                    }
                    //try again in {i} seconds
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException | ExecutionException ignore) {
                    //ignore exception
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
