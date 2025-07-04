package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperAsyncException;
import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import io.kings.framework.component.zookeeper.exception.ZookeeperTransactionException;
import io.kings.framework.component.zookeeper.exception.ZookeeperWatcherException;
import io.kings.framework.data.serializer.Serializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * <p>zookeeper实现 包含分布式一致性和选举能力</p>
 *
 * @author lun.wang
 * @date 2020/4/20 5:39 下午
 * @since v2.7.5
 */
class ZookeeperProvider extends AbstractZookeeper<Serializable> implements KingsZookeeper {

    private static final String NO_PATH = "No Path Been Created For Register Watching";

    private final ZookeeperTransaction<String, Serializable> transaction;
    private final ZookeeperAsync<String, Serializable> async;
    private final Serializer serializer;

    /**
     * 添加连接状态监听器
     *
     * @param listener listener
     * @since v2.8.6
     */
    @Override
    public void connectStateListener(ZookeeperConnectionStateListener listener) {
        if (listener != null) {
            //default listener
            this.curatorFramework.getConnectionStateListenable()
                    .addListener(Zookeeper4DistributedFactory.connectionStateListener(this.threadPool(),
                            listener, this.serializer), this.threadPool());
        }
    }

    /**
     * init method
     */
    @Override
    public void complete() {
        this.curatorFramework.start();
    }

    /**
     * 构造实现
     *
     * @param curatorFramework zk client
     * @param executorService  thread pool
     */
    ZookeeperProvider(CuratorFramework curatorFramework, ExecutorService executorService,
                      Serializer serializer) {
        super(curatorFramework, executorService);
        Assert.notNull(serializer, "[ZookeeperSerializer] is null");
        this.serializer = serializer;
        this.transaction = new ZookeeperTransactionProvider(curatorFramework, executorService,
                serializer);
        this.async = new ZookeeperAsyncProvider(curatorFramework, executorService, serializer);
    }

    /*==========================zookeeper operation==============================*/

    /**
     * 读取一个节点的数据内容 注意，此方法返的返回值是byte[] --> V
     *
     * @param s          key
     * @param serializer 序列化器
     * @return value
     * @throws ZookeeperException failed
     */
    @Override
    public Serializable get(String s, Serializer serializer) throws ZookeeperException {
        try {
            if (!this.contains(s)) {
                return null;
            }
            return this.deserialize(serializer == null ? this.serializer : serializer,
                    curatorFramework.getData().forPath(path0(s)));
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * 获取某个节点的所有子节点路径 注意：该方法的返回值为List,获得ZNode的子节点Path列表。 可以调用额外的方法(监控、后台处理或者获取状态watch, background or
     * get stat) 并在最后调用forPath()指定要操作的父ZNode
     *
     * @param s key
     * @return children keys
     * @throws ZookeeperException failed
     */
    @Override
    public String[] children(String s) throws ZookeeperException {
        try {
            if (!this.contains(s)) {
                throw new ZookeeperException(
                        String.format("No Path been Created for children by:%s", s));
            }
            final List<String> paths = curatorFramework.getChildren().forPath(path0(s));
            if (CollectionUtils.isEmpty(paths)) {
                return new String[0];
            }
            String[] keys = new String[paths.size()];
            return paths.toArray(keys);
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * CuratorFramework的实例包含inTransaction()接口方法，调用此方法开启一个ZooKeeper事务. 可以复合create, setData, check,
     * and/or delete 等操作然后调用commit()作为一个原子操作提交。
     *
     * @param action 操作
     * @return list of ZookeeperTransactionResponse
     * @throws ZookeeperTransactionException failed
     */
    @Override
    public Collection<ZookeeperTransactionResponse> inTransaction(
            Consumer<ZookeeperTransaction<String, Serializable>> action)
            throws ZookeeperTransactionException {
        //先开启事物
        try (final ZookeeperTransaction<String, Serializable> zookeeperTransaction = this.transaction) {
            action.accept(zookeeperTransaction);
            //执行完成 提交事物
            return this.transaction.commit();
        } catch (Exception e) {
            throw new ZookeeperTransactionException(e);
        }
    }

    /**
     * 上面提到的创建、删除、更新、读取等方法都是同步的，Curator提供异步接口，引入了BackgroundCallback 接口用于处理异步接口调用之后服务端返回的结果信息。BackgroundCallback接口中一个重要的回调值为CuratorEvent，里面包含事件类型、响应吗和节点的详细信息。
     *
     * @param action 操作
     * @throws ZookeeperAsyncException failed
     */
    @Override
    public KingsZookeeper inAsync(Consumer<ZookeeperAsync<String, Serializable>> action,
                                  ZookeeperAsyncCallback<String, Serializable> callback,
                                  ZookeeperAsyncErrorListener listener)
            throws ZookeeperAsyncException {
        try (final ZookeeperAsync<String, Serializable> asyncOperator = this.async.openAsync(
                callback, listener)) {
            action.accept(asyncOperator);
            return this;
        } catch (Exception e) {
            throw new ZookeeperAsyncException(e);
        }
    }

    /*==============================register watcher for path=================================*/

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
     * @throws ZookeeperWatcherException 注册时发生的错误
     * @see ZookeeperPathWatcher {@link ZookeeperPathWatcher#nodeChanged(Object, Object)}
     */
    @Override
    public KingsZookeeper registerPathWatcher(final String path,
                                              final ZookeeperPathWatcher<String,
                                                      Serializable> pathListener, Serializer serializer)
            throws ZookeeperWatcherException {
        NodeCache nodeCache = null;
        try {
            if (!this.contains(path)) {
                throw new ZookeeperWatcherException(NO_PATH);
            }
            nodeCache = new NodeCache(this.curatorFramework, path0(path), false);
            final NodeCache node = nodeCache;
            node.getListenable().addListener(() -> {
                // 节点发生变化，回调方法
                if (node.getCurrentData() != null) {
                    final ChildData currentData = node.getCurrentData();
                    pathListener.nodeChanged(currentData.getPath(),
                            this.deserialize(serializer == null ? this.serializer : serializer,
                                    currentData.getData()));
                } else {
                    //避免不调用和空指针
                    pathListener.nodeChanged("", null);
                }
            });
            // 如果为true则首次不会缓存节点内容到cache中，默认为false,设置为true首次不会触发监听事件
            nodeCache.start();
            return this;
        } catch (Exception e) {
            //close io watcher
            Optional.ofNullable(nodeCache).ifPresent(CloseableUtils::closeQuietly);
            throw new ZookeeperWatcherException("RegisterPathWatcher failed ", e);
        }
    }

    /**
     * 对指定的路径节点的一级子目录进行监听，不对该节点的操作进行监听，对其子目录的节点进行增、删、改的操作监听
     *
     * @param path     监听的节点
     * @param listener 监听器
     * @throws ZookeeperWatcherException 创建监听过程中发生的错误
     */
    @Override
    public KingsZookeeper registerPathChildrenWatcher(
            final String path,
            final ZookeeperPathChildrenWatcher<String, Serializable> listener
            , Serializer serializer)
            throws ZookeeperWatcherException {
        PathChildrenCache childrenCache = null;
        try {
            Assert.notNull(listener,
                    "listener not be null to register the watcher for path children ");
            if (!this.contains(path)) {
                throw new ZookeeperException(NO_PATH);
            }
            childrenCache = new PathChildrenCache(this.curatorFramework, path0(path), true, false,
                    executorService);
            childrenCache.getListenable().addListener((c, e) -> {
                if (e == null || e.getType() == null || e.getData() == null) {
                    return;
                }
                final PathChildrenCacheEvent.Type type = e.getType();
                final ChildData data = e.getData();
                final String p = data.getPath();
                final Serializable d =
                        this.deserialize(serializer == null ? this.serializer : serializer,
                                data.getData());
                switch (type) {
                    case CHILD_ADDED:
                        //添加了子节点
                        listener.childAdd(this, p, d);
                        break;
                    case INITIALIZED:
                        //初始化
                        listener.initialized(this, p, d);
                        break;
                    case CHILD_REMOVED:
                        //删除了子节点
                        listener.childRemove(this, p, d);
                        break;
                    case CHILD_UPDATED:
                        //子节点发生变化
                        listener.childUpdate(this, p, d);
                        break;
                    case CONNECTION_LOST:
                        //ZK挂掉一段时间后
                        listener.connectLost(this, p, d);
                        break;
                    case CONNECTION_SUSPENDED:
                        //ZK挂掉
                        listener.connectSuspended(this, p, d);
                        break;
                    case CONNECTION_RECONNECTED:
                        //重新启动ZK curator client is different so will new one
                        listener.connectReconnect(
                                new ZookeeperProvider(c, this.threadPool(),
                                        serializer == null ? this.serializer : serializer), p, d);
                        break;
                    default:
                        break;
                }
            });
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            return this;
        } catch (Exception e) {
            Optional.ofNullable(childrenCache).ifPresent(CloseableUtils::closeQuietly);
            throw new ZookeeperWatcherException("RegisterPathChildrenWatcher failed ", e);
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
    @Override
    public KingsZookeeper registerPathAndChildrenWatcher(
            String path, boolean cacheData, boolean createParentPath,
            int maxDepthWatcher, ExecutorService threadPool,
            ZookeeperPathAndChildrenWatcher<String, Serializable> listener, Serializer serializer)
            throws ZookeeperWatcherException {
        TreeCache treeCache = null;
        try {
            Assert.isTrue(maxDepthWatcher > 0, "maxDepthWatcher mast great by 0 for watcher");
            if (!this.contains(path)) {
                throw new ZookeeperWatcherException(NO_PATH);
            }
            TreeCache.Builder builder = TreeCache.newBuilder(this.curatorFramework, path0(path));
            builder.setExecutor(threadPool);
            builder.setCacheData(cacheData);
            builder.setCreateParentNodes(createParentPath);
            builder.setMaxDepth(maxDepthWatcher);
            treeCache = builder.build();
            treeCache.start();
            treeCache.getListenable().addListener((client, event) -> {
                if (event == null || event.getType() == null || event.getData() == null) {
                    return;
                }
                final ChildData data = event.getData();
                final String p = data.getPath();
                Serializable d = this.deserialize(serializer == null ? this.serializer : serializer,
                        data.getData());
                switch (event.getType()) {
                    case NODE_ADDED:
                        listener.pathAdd(this, p, d);
                        break;
                    case NODE_UPDATED:
                        listener.pathUpdate(this, p, d);
                        break;
                    case NODE_REMOVED:
                        listener.pathRemove(this, p, d);
                        break;
                    case CONNECTION_SUSPENDED:
                        listener.connectSuspended(this, p, d);
                        break;
                    case CONNECTION_RECONNECTED:
                        listener.connectReconnect(
                                new ZookeeperProvider(client, this.threadPool(),
                                        serializer == null ? this.serializer : serializer), p, d);
                        break;
                    case CONNECTION_LOST:
                        listener.connectLost(this, p, d);
                        break;
                    case INITIALIZED:
                        listener.initialized(this, p, d);
                        break;
                    default:
                        break;
                }
            });
            return this;
        } catch (Exception e) {
            Optional.ofNullable(treeCache).ifPresent(CloseableUtils::closeQuietly);
            throw new ZookeeperWatcherException("RegisterPathAndChildrenWatcher failed", e);
        }
    }

    /**
     * 创建一个节点 附带初始化内容 并递归操作
     *
     * @param s          key 不可为null
     * @param s2         value 可为null
     * @param mode       节点模式
     * @param recurse    是否递归操作
     * @param serializer 序列化器
     * @throws ZookeeperException failed
     * @see NodeMode
     */
    @Override
    public ZookeeperWriter<String, Serializable> create(
            String s, Serializable s2, NodeMode mode, boolean recurse, Serializer serializer)
            throws ZookeeperException {
        try {
            Assert.notNull(mode, "NodeMode must not be null");
            if (!this.contains(path0(s))) {
                CreateBuilder builder = curatorFramework.create();
                //create mode
                super.withMode(builder, mode);
                return super.create(builder, recurse, s, s2,
                        Optional.ofNullable(serializer).orElse(this.serializer),
                        this);
            } else {
                throw new ZookeeperException(String.format("Node exists for %s", s));
            }
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * 删除一个节点，强制指定版本进行删除
     *
     * @param s       key
     * @param version version
     * @throws ZookeeperException failed
     */
    @Override
    public ZookeeperWriter<String, Serializable> deleteWithVersion(String s, int version)
            throws ZookeeperException {
        try {
            if (!this.contains(s)) {
                throw new ZookeeperException(
                        String.format("No Path been Created for deleteWithVersion by:%s", s));
            }
            curatorFramework.delete().withVersion(version).forPath(path0(s));
            return this;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * 删除一个节点，强制保证删除 接口是一个保障措施，只要客户端会话有效，那么会在后台持续进行删除操作，直到删除节点成功。
     *
     * @param s key
     * @throws ZookeeperException failed
     */
    @Override
    public ZookeeperWriter<String, Serializable> deleteForce(String s)
            throws ZookeeperException {
        try {
            if (!this.contains(s)) {
                throw new ZookeeperException(
                        String.format("No Path been Created for deleteForce by:%s", s));
            }
            curatorFramework.delete().guaranteed().forPath(path0(s));
            return this;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * 删除一个节点，并且递归删除其所有的子节点
     *
     * @param s       key
     * @param recurse 是否递归
     * @throws ZookeeperException failed
     */
    @Override
    public ZookeeperWriter<String, Serializable> delete(String s, boolean recurse)
            throws ZookeeperException {
        try {
            if (!this.contains(s)) {
                throw new ZookeeperException(
                        String.format("No Path been Created for delete by:%s", s));
            }
            final DeleteBuilder delete = curatorFramework.delete();
            if (recurse) {
                delete.deletingChildrenIfNeeded();
            }
            delete.forPath(path0(s));
            return this;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * 更新一个节点的数据内容，强制指定版本进行更新
     *
     * @param s       key
     * @param s2      value
     * @param version version
     * @throws ZookeeperException failed
     */
    @Override
    public ZookeeperWriter<String, Serializable> update(String s, Serializable s2, Integer version,
                                                        Serializer serializer)
            throws ZookeeperException {
        try {
            if (!this.contains(s)) {
                throw new ZookeeperException(
                        String.format("No Path been Created for update by:%s", s));
            }
            Assert.notNull(s2, "data must not be empty");
            final SetDataBuilder setDataBuilder = curatorFramework.setData();
            if (version != null) {
                setDataBuilder.withVersion(version);
            }
            setDataBuilder
                    .forPath(path0(s),
                            serializer == null ? this.serializer.serialize(s2) : serializer.serialize(s2));
            return this;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * close client at destroy
     *
     * @throws ZookeeperException close failed
     */
    @Override
    public void close() throws ZookeeperException {
        if (this.async != null) {
            this.async.close();
        }
        if (this.transaction != null) {
            this.transaction.close();
        }
        Optional.of(this.curatorFramework).ifPresent(CloseableUtils::closeQuietly);
        Optional.of(this.executorService).ifPresent(ExecutorService::shutdown);
    }
}
