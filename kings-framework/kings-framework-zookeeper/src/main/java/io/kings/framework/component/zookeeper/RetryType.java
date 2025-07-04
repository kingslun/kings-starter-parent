package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.config.ZookeeperProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;
import org.springframework.util.Assert;

/**
 * <p>zookeeper client连接server的重试策略
 * 屏蔽curator框架策略</p>
 *
 * @author lun.wang
 * @date 2020/4/20 5:10 下午
 * @since v2.7.5
 */
public enum RetryType {

    /**
     * RetryUntilElapsed(int maxElapsedTimeMs, int sleepMsBetweenRetries)
     * 以sleepMsBetweenRetries的间隔重连,直到超过maxElapsedTimeMs的时间设置
     */
    RETRY_UNTIL_ELAPSED {
        @Override
        public RetryPolicy policy(ZookeeperProperties.Retry retry) {
            Assert.notNull(retry, ERROR_MSG);
            return new RetryUntilElapsed(
                    retry.getRetryCount() * retry.getSleepMsBetweenRetries(),
                    retry.getSleepMsBetweenRetries());
        }
    },

    /**
     * RetryNTimes(int n, int sleepMsBetweenRetries) 指定重连次数
     */
    RETRY_N_TIMES {
        @Override
        public RetryPolicy policy(ZookeeperProperties.Retry retry) {
            Assert.notNull(retry, ERROR_MSG);
            return new RetryNTimes(retry.getRetryCount(),
                    retry.getSleepMsBetweenRetries());
        }
    },

    /**
     * RetryOneTime(int sleepMsBetweenRetry) 重连一次,简单粗暴
     */
    RETRY_ONE_TIME {
        @Override
        public RetryPolicy policy(ZookeeperProperties.Retry retry) {
            Assert.notNull(retry, ERROR_MSG);
            return new RetryOneTime(retry.getSleepMsBetweenRetries());
        }
    },

    /**
     * ExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries) ExponentialBackoffRetry(int
     * baseSleepTimeMs, int maxRetries, int maxSleepMs)
     * <p>
     * 时间间隔 = baseSleepTimeMs * Math.max(1, random.nextInt(1 << (retryCount + 1)))
     */
    EXPONENTIAL_BACKOFF_RETRY {
        @Override
        public RetryPolicy policy(ZookeeperProperties.Retry retry) {
            Assert.notNull(retry, ERROR_MSG);
            return new ExponentialBackoffRetry(
                    retry.getSleepMsBetweenRetries(),
                    retry.getRetryCount());
        }
    };

    private static final String ERROR_MSG = "retry is null";

    public abstract RetryPolicy policy(ZookeeperProperties.Retry retry);

}
