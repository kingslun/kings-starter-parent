package io.kings.framework.util.thread;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * <p>线程池创建</p>
 * 创建各种类型的线程池
 *
 * @author : lun.wang
 * @date : 2020/04/10 14:40:06
 * @see Executors
 * @see ExecutorService
 **/
public final class ThreadPool {

    /**
     * 默认核心工作线程数 读取硬件线程数
     */
    public static final int CORE_POOL_SIZE;
    /**
     * 空闲线程在接受任务前最大空闲时间 即到了时间仍未接到工作则会被线程池销毁 默认60毫秒
     */
    public static final long KEEP_ALIVE_TIME;
    /**
     * 空闲线程在接受任务前最大空闲时间单位
     *
     * @see this#KEEP_ALIVE_TIME
     */
    public static final TimeUnit KEEP_ALIVE_TIME_TIMEUNIT;
    /**
     * 工作线程队列长度 默认2E10
     */
    public static final int WORK_QUEUE_SIZE;

    static {
        CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
        KEEP_ALIVE_TIME = 60L;
        WORK_QUEUE_SIZE = 1024;
        KEEP_ALIVE_TIME_TIMEUNIT = TimeUnit.MILLISECONDS;
    }

    private ThreadPool() {
        throw new AssertionError("Can't be initialized Error");
    }

    /**
     * 创建设备线程数量的线程池
     *
     * @param threadName 线程名称
     * @return ExecutorService
     */
    public static ExecutorService availableThreadPool(String threadName) {
        //Common Thread Pool
        final int poolSize = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                ThreadFactory.defaultThreadFactory(threadName),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 创建指定线程数量的线程池 工作队列最大1024且默认闲置线程不等待
     *
     * @param corePoolSize    同时工作的线程数
     * @param maximumPoolSize 最大接受的同时工作线程数
     * @param threadName      线程名称
     * @return ExecutorService
     */
    public static ExecutorService threadPool(String threadName, int corePoolSize,
                                             int maximumPoolSize) {
        //Common Thread Pool
        return threadPool(threadName, corePoolSize, maximumPoolSize, 0L, 1024);
    }

    /**
     * 创建指定线程数量的线程池
     *
     * @param corePoolSize    同时工作的线程数
     * @param maximumPoolSize 最大接受的同时工作线程数
     * @param threadName      线程名称
     * @param keepAliveTime   当工作线程数大于核心数量 这是多余的空闲线程在终止之前等待新任务的最长时间。默认不等待
     * @param workQueueSize   最大能接受的工作线程数量
     * @return ExecutorService
     */
    public static ExecutorService threadPool(String threadName, int corePoolSize,
                                             int maximumPoolSize, long keepAliveTime,
                                             int workQueueSize) {
        //Common Thread Pool
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(workQueueSize),
                ThreadFactory.defaultThreadFactory(threadName),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * cpu密集地线程池 CPU密集型的任务配置尽可能少的线程数量： 一般公式：CPU核数+1个线程的线程数
     *
     * @param threadName    线程名称
     * @param workQueueSize 工作线程队列长度 超出等待长度的任务会被丢弃
     * @return ExecutorService
     */
    public static ExecutorService cpuDenselyPoolWithWorkQueueSize(String threadName,
                                                                  int workQueueSize) {
        //cpu densely Thread Pool
        return new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE + 1,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_TIMEUNIT,
                new LinkedBlockingQueue<>(workQueueSize),
                ThreadFactory.defaultThreadFactory(threadName),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * cpu密集地线程池 CPU密集型的任务配置尽可能少的线程数量： 一般公式：CPU核数+1个线程的线程数
     *
     * @param threadName 线程名称
     * @return ExecutorService
     */
    public static ExecutorService cpuDenselyPool(String threadName) {
        //cpu densely Thread Pool
        return cpuDenselyPoolWithWorkQueueSize(threadName, WORK_QUEUE_SIZE);
    }

    /**
     * IO密集型线程池 IO密集分两种 一种是大量阻塞的另一种是很少有阻塞的 计算公式不一样 B(阻塞的IO密集型): IO密集型，即任务需要大量的IO，即大量的阻塞。
     * 在单线程上运行IO密集型的任务会导致浪费大量的CPU运算能力浪费在等待。所以在IO 密集型任务中使用多线程可以大大的加速程序运行。故需要·多配置线程数：
     * 参考公式：CPU核数/（1-阻塞系数 ） 阻塞系数在（0.8-0.9）之间 这里取用0.85
     *
     * @param threadName 线程名称
     * @return ExecutorService
     */
    public static ExecutorService blockingIoDenselyPool(String threadName) {
        //blocking io densely Thread Pool
        return blockingIoDenselyPoolWithWorkQueueSize(threadName, WORK_QUEUE_SIZE);
    }

    /**
     * 创建指定名称的线程池 工作线程数量为CPU核数 最大工作线程数以计算结果为准 工作线程长度为指定长度 且空闲线程存活时间为60毫秒 IO密集型线程池 IO密集分两种
     * 一种是大量阻塞的另一种是很少有阻塞的 计算公式不一样 B(阻塞的IO密集型): IO密集型，即任务需要大量的IO，即大量的阻塞。
     * 在单线程上运行IO密集型的任务会导致浪费大量的CPU运算能力浪费在等待。所以在IO 密集型任务中使用多线程可以大大的加速程序运行。故需要·多配置线程数：
     * 参考公式：CPU核数/（1-阻塞系数 ） 阻塞系数在（0.8-0.9）之间 这里取用0.85
     *
     * @param threadName 线程名称
     * @return ExecutorService
     */
    public static ExecutorService blockingIoDenselyPoolWithWorkQueueSize(String threadName,
                                                                         int workQueueSize) {
        //blocking io densely Thread Pool
        return new ThreadPoolExecutor(CORE_POOL_SIZE,
                BigDecimal.valueOf(CORE_POOL_SIZE / 0.15).intValue(),
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_TIMEUNIT,
                new LinkedBlockingQueue<>(workQueueSize),
                ThreadFactory.defaultThreadFactory(threadName),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * IO密集型线程池 IO密集分两种 一种是大量阻塞的另一种是很少有阻塞的 计算公式不一样 A(非阻塞的IO密集型): 由于IO密集型任务的线程并不是一直在执行任务，则应配置尽可能多的线程，如CPU核数*2
     *
     * @param threadName 线程名称
     * @return ExecutorService
     */
    public static ExecutorService nonblockingIoDenselyPool(String threadName) {
        //nonblocking io densely Thread Pool
        return nonblockingIoDenselyPool(threadName, WORK_QUEUE_SIZE);
    }

    /**
     * 创建指定名称的线程池 工作线程数量为CPU核数 最大工作线程数以计算结果为准 工作线程长度为指定长度 且空闲线程存活时间为60毫秒
     * <p>
     * IO密集型线程池 IO密集分两种 一种是大量阻塞的另一种是很少有阻塞的 计算公式不一样 A(非阻塞的IO密集型): 由于IO密集型任务的线程并不是一直在执行任务，则应配置尽可能多的线程，如CPU核数*2
     *
     * @param threadName    线程名称
     * @param workQueueSize 工作线程队列长度
     * @return ExecutorService
     */
    public static ExecutorService nonblockingIoDenselyPool(String threadName, int workQueueSize) {
        //nonblocking io densely Thread Pool
        return new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE * 2,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_TIMEUNIT,
                new LinkedBlockingQueue<>(workQueueSize),
                ThreadFactory.defaultThreadFactory(threadName),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
