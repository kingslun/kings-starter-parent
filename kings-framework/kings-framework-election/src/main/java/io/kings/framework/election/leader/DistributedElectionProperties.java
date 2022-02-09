package io.kings.framework.election.leader;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author lun.wang
 * @date 2021/6/28 1:47 下午
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = DistributedElectionProperties.ELECTION_PREFIX)
public class DistributedElectionProperties {

    static final String ELECTION_PREFIX = "kings.election";
    public static final String ELECTION_TYPE_PREFIX = ELECTION_PREFIX + ".type";
    public static final String ELECTION_SWITCH_PREFIX = ELECTION_PREFIX + ".enabled";
    /**
     * leader选举开关 为true则开启选举功能
     */
    private boolean enabled = true;

    /**
     * 选举实现类型 默认用Redis 目前仅此一种实现
     */
    private Type type = Type.REDIS;
    @NestedConfigurationProperty
    private Redis redis = new Redis();

    @Getter
    @Setter
    @ToString
    static class Redis {

        /**
         * cluster key
         */
        private String electionThreadName = "Leader-Election";
        /**
         * select key
         */
        private String groups = "master-election";
        /**
         * slave轮询选举时间
         */
        private int initialDelay = 5;
        private int interval = 5;
        private TimeUnit intervalUnit = TimeUnit.SECONDS;
    }

    @NestedConfigurationProperty
    private Zookeeper zookeeper = new Zookeeper();

    /**
     * leader选举配置
     */
    @Getter
    @Setter
    @ToString
    public static class Zookeeper {

        /**
         * zk选举使用的path
         */
        private String path;
    }

    /**
     * 分布式选举实现 目前有且仅有基于Redis和zookeeper的实现
     */
    enum Type {
        REDIS, ZOOKEEPER
    }
}
