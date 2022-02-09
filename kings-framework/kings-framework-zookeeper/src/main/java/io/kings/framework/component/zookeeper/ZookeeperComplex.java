package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperAsyncException;
import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import io.kings.framework.component.zookeeper.exception.ZookeeperTransactionException;
import io.kings.framework.component.zookeeper.exception.ZookeeperWatcherException;
import io.kings.framework.data.serializer.Serializer;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * zookeeper操作对象 zookeeper服务节点操作客户端实现 说明：
 * <p>
 * 实现节点的增删改查、节点监听、及事物和异步操作集的封装 满足多线程操作,线程安全问题依赖curator 并对curator完全封装 屏蔽所有的api和zk相关功能
 * 开发者只需关注此对象对节点的操作即可无需关注zookeeper以及开源curator的实现和版本变动
 * </p>
 *
 * @author lun.wang
 * @date 2020/4/20 5:49 下午
 * @since v2.7.5
 */
public interface ZookeeperComplex<K, V> extends Zookeeper {

    /*=================================transaction operation===================================*/

    /**
     * CuratorFramework的实例包含inTransaction()接口方法，调用此方法开启一个ZooKeeper事务. 可以复合create, setData, check,
     * and/or delete 等操作然后调用commit()作为一个原子操作提交。
     *
     * @param action 操作
     * @return list of ZookeeperTransactionResponse
     * @throws ZookeeperTransactionException failed
     */
    Collection<ZookeeperTransactionResponse> inTransaction(
        Consumer<ZookeeperTransaction<K, V>> action)
        throws ZookeeperTransactionException;

    /*=================================async operation===================================*/

    /**
     * 上面提到的创建、删除、更新、读取等方法都是同步的，Curator提供异步接口，引入了BackgroundCallback 接口用于处理异步接口调用之后服务端返回的结果信息。BackgroundCallback接口中一个重要的回调值为CuratorEvent，里面包含事件类型、响应吗和节点的详细信息。
     *
     * @param action   操作
     * @param callback callback for operate
     * @param listener listen for the error
     * @return ZookeeperComplex this
     * @throws ZookeeperAsyncException failed
     */
    KingsZookeeper inAsync(Consumer<ZookeeperAsync<K, V>> action,
        ZookeeperAsyncCallback<K, V> callback,
        ZookeeperAsyncErrorListener listener) throws ZookeeperAsyncException;

    /*==============================watcher for path================================*/

    /**
     * 注册一个指定节点监听器 监听事件包括指定的路径节点的增、删、改的操作 响应的操作会触发监听器的api
     *
     * <p>
     * * 说明： * * 节点路径不存在，set不触发监听 * 节点路径不存在，创建事件触发监听（第一次创建时要触发） * 节点路径存在，set触发监听（改操作触发） *
     * 节点路径存在，delete触发监听（删操作触发） * * 节点挂掉，未触发任何监听 * 节点重连，未触发任何监听 * 节点重连 ，恢复监听 *
     * </p>
     *
     * @param path         监听的路径
     * @param pathListener 监听器
     * @return this
     * @throws ZookeeperWatcherException 注册时发生的错误
     * @see ZookeeperPathWatcher {@link ZookeeperPathWatcher#nodeChanged(Object, Object)}
     */
    default KingsZookeeper registerPathWatcher(K path, ZookeeperPathWatcher<K, V> pathListener)
        throws ZookeeperWatcherException {
        return this.registerPathWatcher(path, pathListener, null);
    }

    /**
     * 节点监听注册
     *
     * @param path         path
     * @param pathListener listener
     * @param serializer   序列化器
     * @return this
     * @throws ZookeeperWatcherException failed
     */
    KingsZookeeper registerPathWatcher(K path, ZookeeperPathWatcher<K, V> pathListener,
        Serializer serializer)
        throws ZookeeperWatcherException;

    /**
     * 对指定的路径节点的一级子目录进行监听，不对该节点的操作进行监听，对其子目录的节点进行增、删、改的操作监听 *  说明 *  注册监听器，当前节点不存在，创建该节点：未抛出异常及错误日志
     * *  注册子节点触发type=[CHILD_ADDED] *  更新触发type=[CHILD_UPDATED] * *  zk挂掉type=CONNECTION_SUSPENDED,，一段时间后type=CONNECTION_LOST
     * *  重启zk：type=CONNECTION_RECONNECTED, data=null *  更新子节点：type=CHILD_UPDATED,
     * data=ChildData{path='/zktest111/tt1', stat=4294979983, *  4294979993,1501037475236,1501037733805,2,0,0,0,6,0,4294979983
     * *  ,data=[55, 55, 55, 55, 55, 55]} ​ *  删除子节点type=CHILD_REMOVED *  更新根节点：不触发 *  删除根节点：不触发
     * 无异常 *  创建根节点：不触发 *  再创建及更新子节点不触发 * *  重启时，与zk连接失败
     *
     * @param path     监听的节点
     * @param listener 监听器
     * @return this
     * @throws ZookeeperWatcherException 创建监听过程中发生的错误
     */
    default KingsZookeeper registerPathChildrenWatcher(K path,
        ZookeeperPathChildrenWatcher<K, V> listener)
        throws ZookeeperWatcherException {
        return this.registerPathChildrenWatcher(path, listener, null);
    }

    /**
     * 节点监听事件
     *
     * @param path       监听节点
     * @param listener   监听器
     * @param serializer 序列化器
     * @return this
     * @throws ZookeeperWatcherException failed
     */
    KingsZookeeper registerPathChildrenWatcher(K path, ZookeeperPathChildrenWatcher<K, V> listener,
        Serializer serializer)
        throws ZookeeperWatcherException;

    /**
     * overload method
     *
     * @param path             path
     * @param maxDepthWatcher  max watcher depth
     * @param createParentPath create parent path
     * @param cacheData        cache data
     * @param watcher          watcher
     * @return this
     * @throws ZookeeperWatcherException register watcher fail
     * @see this#registerPathAndChildrenWatcher(Object, boolean, boolean, int, ExecutorService,
     * ZookeeperPathAndChildrenWatcher)
     */
    default KingsZookeeper registerPathAndChildrenWatcher(K path, boolean cacheData,
        boolean createParentPath,
        int maxDepthWatcher,
        ZookeeperPathAndChildrenWatcher<K, V> watcher)
        throws ZookeeperWatcherException {
        try {
            return this.registerPathAndChildrenWatcher(path, cacheData, createParentPath,
                maxDepthWatcher, this.threadPool(), watcher);
        } catch (ZookeeperException e) {
            throw new ZookeeperWatcherException(e);
        }
    }

    /**
     * overload method
     *
     * @param path       path
     * @param serializer data serializer
     * @param watcher    watcher
     * @return this
     * @throws ZookeeperWatcherException register watcher fail
     * @see this#registerPathAndChildrenWatcher(Object, boolean, boolean, int, ExecutorService,
     * ZookeeperPathAndChildrenWatcher)
     */
    default KingsZookeeper registerPathAndChildrenWatcher(K path,
        ZookeeperPathAndChildrenWatcher<K, V> watcher,
        Serializer serializer)
        throws ZookeeperWatcherException {
        try {
            return this.registerPathAndChildrenWatcher(path, true, true,
                Integer.MAX_VALUE, this.threadPool(), watcher, serializer);
        } catch (ZookeeperException e) {
            throw new ZookeeperWatcherException(e);
        }
    }

    /**
     * overload method
     *
     * @param path            path
     * @param maxDepthWatcher max watcher depth
     * @param watcher         watcher
     * @return this
     * @throws ZookeeperWatcherException register watcher fail
     * @see this#registerPathAndChildrenWatcher(Object, boolean, boolean, int, ExecutorService,
     * ZookeeperPathAndChildrenWatcher)
     */
    default KingsZookeeper registerPathAndChildrenWatcher(K path, int maxDepthWatcher,
        ZookeeperPathAndChildrenWatcher<K, V> watcher)
        throws ZookeeperWatcherException {
        try {
            return this.registerPathAndChildrenWatcher(path, true, true, maxDepthWatcher,
                this.threadPool(), watcher);
        } catch (ZookeeperException e) {
            throw new ZookeeperWatcherException(e);
        }
    }

    /**
     * overload method
     *
     * @param path    path
     * @param watcher watcher
     * @return this
     * @throws ZookeeperWatcherException register watcher fail
     * @see this#registerPathAndChildrenWatcher(Object, boolean, boolean, int, ExecutorService,
     * ZookeeperPathAndChildrenWatcher)
     */
    default KingsZookeeper registerPathAndChildrenWatcher(K path,
        ZookeeperPathAndChildrenWatcher<K, V> watcher)
        throws ZookeeperWatcherException {
        try {
            return this.registerPathAndChildrenWatcher(path, true, true, Integer.MAX_VALUE,
                this.threadPool(), watcher);
        } catch (ZookeeperException e) {
            throw new ZookeeperWatcherException(e);
        }
    }

    /**
     * 可以将指定的路径节点作为根节点（祖先节点）对其所有的子节点操作进行监听 呈现树形目录的监听 可以设置监听深度 最大监听深度为2147483647（int类型的最大值） * 说明 *
     * TreeCache.nodeState == LIVE的时候，才能执行getCurrentChildren非空,默认为PENDING * 初始化完成之后，监听节点操作时
     * TreeCache.nodeState == LIVE * * maxDepth值设置说明，比如当前监听节点/t1，目录最深为/t1/t2/t3/t4,则maxDepth=3,说明下面3级子目录全
     * * 监听，即监听到t4，如果为2，则监听到t3,对t3的子节点操作不再触发 * maxDepth最大值2147483647 * *
     * 初次开启监听器会把当前节点及所有子目录节点，触发[type=NODE_ADDED]事件添加所有节点（小等于maxDepth目录） * 默认监听深度至最低层 *
     * 初始化以[type=INITIALIZED]结束 * *  [type=NODE_UPDATED],set更新节点值操作，范围[当前节点，maxDepth目录节点](闭区间) * *
     * [type=NODE_ADDED] 增加节点 范围[当前节点，maxDepth目录节点](左闭右闭区间) * *  [type=NODE_REMOVED] 删除节点， 范围[当前节点，
     * maxDepth目录节点](闭区间),删除当前节点无异常 * *  事件信息 *  TreeCacheEvent{type=NODE_ADDED,
     * data=ChildData{path='/zktest1', stat=4294979373,4294979373,1499850881635,1499850881635,0,0,0,0,2,0,4294979373
     * , data=[116, 49]}}
     *
     * @param path             监听的节点
     * @param listener         监听器
     * @param cacheData        是否缓存节点值
     * @param createParentPath 是否创建父节点
     * @param threadPool       执行工作的线程池
     * @param maxDepthWatcher  最大监听深度 默认为 int最大值
     * @return this
     * @throws ZookeeperWatcherException 创建监听过程中发生的错误
     */
    default KingsZookeeper registerPathAndChildrenWatcher(K path, boolean cacheData,
        boolean createParentPath,
        int maxDepthWatcher,
        ExecutorService threadPool,
        ZookeeperPathAndChildrenWatcher<K, V> listener)
        throws ZookeeperWatcherException {
        return this.registerPathAndChildrenWatcher(path, cacheData, createParentPath,
            maxDepthWatcher, threadPool,
            listener, null);
    }

    /**
     * 支持序列化器的节点监听器注册API
     *
     * @param path             节点
     * @param cacheData        是否缓存
     * @param createParentPath 是否创建父节点
     * @param maxDepthWatcher  最大监听深度
     * @param threadPool       执行线程池
     * @param listener         监听器
     * @param serializer       序列化器
     * @return this
     * @throws ZookeeperWatcherException failed
     * @since v2.8.6
     */
    KingsZookeeper registerPathAndChildrenWatcher(K path, boolean cacheData,
        boolean createParentPath,
        int maxDepthWatcher,
        ExecutorService threadPool,
        ZookeeperPathAndChildrenWatcher<K, V> listener,
        Serializer serializer)
        throws ZookeeperWatcherException;
}
