package io.kings.framework.component.zookeeper.config;

import io.kings.framework.component.zookeeper.RetryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * <p>zookeeper自动装配配置文件</p>
 *
 * @author lun.wang
 * @date 2020/4/20 5:05 下午
 * @since v2.7.5
 */
@ConfigurationProperties(ZookeeperProperties.ZK_PREFIX)
@Getter
@Setter
@ToString
public class ZookeeperProperties {

    public static final String ZK_PREFIX = "kings.zookeeper";
    public static final String ZK_ELECTION_PREFIX = ZK_PREFIX + ".leaderElection";
    /**
     * 服务器列表，格式host1:port1,host2:port2,…
     */
    private String host;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 重试策略 四种重试策略
     */
    private RetryType retryType = RetryType.EXPONENTIAL_BACKOFF_RETRY;

    /**
     * 会话超时时间，单位毫秒，默认60000ms
     */
    private int sessionTimeoutMs = 60000;

    /**
     * 连接创建超时时间，单位毫秒，默认60000ms
     */
    private int connectionTimeoutMs = 60000;

    /**
     * 只读 如果为true则 create update delete操作为报错
     */
    private boolean readOnly;
    /**
     * 断连重试配置
     */
    @NestedConfigurationProperty
    private Retry retry = new Retry();

    /**
     * 是否监听连接状态
     */
    private boolean listenConnectState;

    @Getter
    @Setter
    @ToString
    public static class Retry {

        /**
         * 重试次数 默认3次
         */
        int retryCount = 3;

        /**
         * 重试间隔数 默认1000毫秒
         */
        int sleepMsBetweenRetries = 1000;
    }

    /**
     * zk client工作线程配置
     */
    @NestedConfigurationProperty
    private Threads threads = new Threads();

    /**
     * zookeeper 线程池配置
     */
    @Getter
    @Setter
    @ToString
    static class Threads {

        /**
         * thread name for zookeeper
         */
        private String name = "KingsZookeeper";
        /**
         * 核心工作线程数
         */
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        /**
         * 最大接受工作线程数量
         */
        private int maximumPoolSize = Runtime.getRuntime().availableProcessors();

        /**
         * 当工作线程数大于核心数量 这是多余的空闲线程在终止之前等待新任务的最长时间。默认不等待
         */
        private long keepAliveTime = 0L;

        /**
         * 工作线程可接受的最大数量
         */
        private int workQueueSize = 1024;
    }

    /*=================================leader election properties=================================*/
}
