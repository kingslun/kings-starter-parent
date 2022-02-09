package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import io.kings.framework.component.zookeeper.exception.ZookeeperSerializeException;
import io.kings.framework.core.bean.BeanLifecycle;
import io.kings.framework.data.exception.SerializeException;
import io.kings.framework.data.serializer.Serializer;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <p>最基础的抽象</p>
 *
 * @author lun.wang
 * @date 2020/4/30 6:07 下午
 * @since v2.7.4
 */
interface Zookeeper extends AutoCloseable, BeanLifecycle {

    /**
     * 内部线程池获取 可以暴露出去给外部使用
     *
     * @return Executor
     * @throws ZookeeperException failed null pointer
     * @see ExecutorService
     * @see java.util.concurrent.Executor
     */
    ExecutorService threadPool() throws ZookeeperException;

    /**
     * close client or other close work
     *
     * @throws ZookeeperException close failed
     */
    @Override
    void close() throws ZookeeperException;

    /**
     * 销毁函数
     *
     * @throws ZookeeperException failure
     */
    @Override
    default void destroy() throws ZookeeperException {
        this.close();
    }

    /**
     * path检测及 自动拼接/
     *
     * @param path source not empty
     * @return src
     */
    default String path0(String path) {
        final String c = "/";
        String inputPath = path.startsWith(c) ? path : c + path;
        return StringUtils.hasText(path) ? inputPath : c;
    }

    /**
     * zk data 序列化
     *
     * @param serializer 序列化器
     * @param data       序列化数据
     * @param <D>        返回数据类型
     * @param <S>        序列器类型
     * @return must extend Serializable
     * @throws SerializeException deserialize failed
     * @author lun.wang
     * @see Serializable
     * @see Serializer
     * @see ZookeeperSerializeException
     * @since v2.8.6
     */
    default <D extends Serializable, S extends Serializer> D deserialize(
        S serializer, byte[] data) throws SerializeException {
        Assert.notNull(serializer, "[ZookeeperSerializer] is null");
        if (data == null || data.length <= 0) {
            return null;
        }
        return serializer.deserialize(data);
    }
}
