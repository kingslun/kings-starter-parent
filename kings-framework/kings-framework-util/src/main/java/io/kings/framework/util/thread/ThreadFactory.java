package io.kings.framework.util.thread;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <p>创建标准的线程工厂 octopus标准</p>
 *
 * @author : lun.wang
 * @date : 2020/04/10 11:07:03
 **/
public final class ThreadFactory implements java.util.concurrent.ThreadFactory {

    /**
     * 线程池编号 -所有工厂类公用此变量 累加
     */
    private static final AtomicInteger POOL_NUMBER;

    static {
        POOL_NUMBER = new AtomicInteger(1);
    }

    /**
     * 线程组
     */
    private final ThreadGroup group;

    /**
     * 单个线程池内线程编号
     */
    private final AtomicInteger threadNumber;

    /**
     * 展示的线程名称
     */
    private final String namePrefix;

    /**
     * 是否在线程名称当中显示线程的编号 默认展示 可在构造器中关闭
     */
    private final boolean showThreadNumber;

    /**
     * 线程名称的分隔符 默认'-' 可定义
     */
    private final char separators;

    /**
     * 私有最终构造器
     *
     * @param poolName          线程池名称 默认pool
     * @param showPoolNumber    线程名称当中是否显示线程池编号 所有的线程池公用一个起始编号 默认true
     * @param poolNumberIndex   线程池编号起始值 默认 0
     * @param threadName        线程名称 默认 thread
     * @param showThreadNumber  是否展示线程编号 默认true
     * @param threadNumberIndex 线程编号起始值 默认1
     * @param separators        线程名称中分隔符 默认'-'
     */
    private ThreadFactory(String poolName, boolean showPoolNumber, int poolNumberIndex,
        String threadName, boolean showThreadNumber, int threadNumberIndex,
        char separators) {
        Assert.hasText(threadName, "ThreadFactory must have a thread name");
        threadNumber = new AtomicInteger(threadNumberIndex > 0 ? threadNumberIndex : 1);
        this.showThreadNumber = showThreadNumber;
        this.separators = separators;
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        StringBuilder prefix = new StringBuilder();
        if (StringUtils.hasText(poolName)) {
            prefix.append(poolName);
            if (showPoolNumber) {
                prefix.append(this.separators)
                    .append(POOL_NUMBER.getAndIncrement() + poolNumberIndex >= 0 ?
                        poolNumberIndex : 0);
            }
            prefix.append(this.separators);
        }
        prefix.append(threadName);
        this.namePrefix = prefix.toString();
    }

    /**
     * 默认构造没有线程池名称  由于log框架日志线程名称长度限制 推荐使用此构造 线程名格式 [thread-1]
     *
     * @param threadName 线程名称
     */
    public static java.util.concurrent.ThreadFactory defaultThreadFactory(String threadName) {
        return new ThreadFactory(null, false, 0, threadName, true, 1, '-');
    }

    /**
     * 默认构造 线程名格式 *[pool-1-${threadName}-1]
     *
     * @param threadName 线程名称
     */
    public static java.util.concurrent.ThreadFactory namedThreadFactory(String threadName) {
        return new ThreadFactory("pool", true, 0, threadName, true, 1, '-');
    }

    /**
     * 默认构造 线程名格式 *[pool-1-${threadName}-${threadNumberIndex}]
     *
     * @param threadName        线程名称
     * @param threadNumberIndex 线程编号开始下标
     */
    public static java.util.concurrent.ThreadFactory namedThreadFactoryWithIndex(String threadName,
        int threadNumberIndex) {
        return new ThreadFactory("pool", true, 0, threadName, true, threadNumberIndex, '-');
    }

    /**
     * 默认构造 线程名格式 *[${poolName}-${threadName}]
     *
     * @param poolName   线程池名称
     * @param threadName 线程名称
     */
    public static java.util.concurrent.ThreadFactory namedPoolThreadFactoryWithoutNumber(String poolName,
        String threadName) {
        return new ThreadFactory(poolName, false, 0, threadName, false, 1, '-');
    }

    /**
     * 默认构造 线程名格式 *[${poolName}-1-${threadName}-1]
     *
     * @param poolName   线程池名称
     * @param threadName 线程名称
     */
    public static java.util.concurrent.ThreadFactory namedPoolThreadFactory(String poolName, String threadName) {
        return new ThreadFactory(poolName, true, 0, threadName, true, 1, '-');
    }

    /**
     * 默认构造 线程名格式 *[${poolName}-1-${threadName}-1]
     *
     * @param poolName   线程池名称
     * @param threadName 线程名称
     */
    public static java.util.concurrent.ThreadFactory namedPoolThreadFactoryWithSeparator(String poolName,
        String threadName, char separators) {
        return new ThreadFactory(poolName, true, 0, threadName, true, 1, separators);
    }

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize priority, name, daemon
     * status, {@code ThreadGroup}, etc.
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to create a thread is rejected
     */
    @Override
    public Thread newThread(@NonNull Runnable r) {
        Objects.requireNonNull(r);
        String threadName =
            this.showThreadNumber ?
                this.namePrefix + this.separators + this.threadNumber.getAndIncrement() :
                this.namePrefix;
        Thread t = new Thread(this.group, r, threadName, 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
