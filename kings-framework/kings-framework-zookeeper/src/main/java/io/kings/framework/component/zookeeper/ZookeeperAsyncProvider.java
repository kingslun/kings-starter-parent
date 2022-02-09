package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import io.kings.framework.data.exception.SerializeException;
import io.kings.framework.data.serializer.Serializer;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.zookeeper.KeeperException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 异步操作实现
 *
 * @author lun.wang
 * @date 2020/4/24 3:32 下午
 * @since v2.7.2
 */
class ZookeeperAsyncProvider extends AbstractZookeeper<Serializable>
    implements ZookeeperAsync<String, Serializable>, BackgroundCallback,
    UnhandledErrorListener, ZookeeperReader<String, Serializable> {

    protected ZookeeperAsyncProvider(CuratorFramework curatorFramework,
        ExecutorService threadPool, Serializer serializer) {
        super(curatorFramework, threadPool);
        this.serializer = serializer;
    }

    /**
     * zk 读写对象序列化工具
     */
    private final Serializer serializer;

    /**
     * init method
     */
    @Override
    public void complete() {
        //初始化方法,空实现
    }

    /**
     * 成功异步回调
     */
    private ZookeeperAsyncCallback<String, Serializable> operatorCallback;
    /**
     * 失败异步回调
     */
    private ZookeeperAsyncErrorListener errorListener;

    /**
     * open async
     *
     * @param operatorCallback 成功异步回调
     * @param errorListener    失败异步回调
     * @return this
     */
    @Override
    public ZookeeperAsync<String, Serializable> openAsync(
        ZookeeperAsyncCallback<String, Serializable> operatorCallback,
        ZookeeperAsyncErrorListener errorListener) {
        this.operatorCallback = operatorCallback;
        this.errorListener = errorListener;
        return this;
    }

    /**
     * 异步回调函数
     *
     * @param curatorFramework curator callback
     * @param curatorEvent     event
     * @throws Exception failed
     */
    @Override
    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent)
        throws Exception {
        if (this.operatorCallback != null) {
            operatorCallback.call(this, convert(curatorEvent));
        }
    }

    @Override
    public void unhandledError(String s, Throwable throwable) {
        Optional.ofNullable(this.errorListener)
            .ifPresent(listener -> listener.onFail(throwable, s));
    }

    /**
     * convert the event data
     *
     * @param curatorEvent curator data
     * @return ZookeeperAsyncResponse
     */
    private ZookeeperAsyncResponse convert(CuratorEvent curatorEvent) throws SerializeException {
        ZookeeperAsyncResponse.ZookeeperAsyncResponseBuilder builder =
            ZookeeperAsyncResponse.builder();
        switch (curatorEvent.getType()) {
            case SET_DATA:
                builder.asyncType(ZookeeperAsyncType.UPDATE);
                break;
            case DELETE:
                builder.asyncType(ZookeeperAsyncType.DELETE);
                break;
            case CREATE:
                builder.asyncType(ZookeeperAsyncType.CREATE);
                break;
            case SYNC:
                builder.asyncType(ZookeeperAsyncType.SYNC);
                break;
            case EXISTS:
                builder.asyncType(ZookeeperAsyncType.NONEXISTENT);
                break;
            case CLOSING:
                builder.asyncType(ZookeeperAsyncType.CLOSING);
                break;
            case CHILDREN:
                builder.asyncType(ZookeeperAsyncType.CHILDREN);
                break;
            case GET_DATA:
                builder.asyncType(ZookeeperAsyncType.GET);
                break;
            case WATCHED:
                builder.asyncType(ZookeeperAsyncType.WATCHED);
                break;
            default:
        }
        builder.children(curatorEvent.getChildren());
        builder.key(curatorEvent.getPath());
        builder.name(curatorEvent.getName());
        switch (curatorEvent.getResultCode()) {
            case 0:
                builder.asyncStatus(ZookeeperAsyncStatus.SUCCESS);
                break;
            case -4:
                builder.asyncStatus(ZookeeperAsyncStatus.CONNECTION_LOSS);
                break;
            case -110:
                builder.asyncStatus(ZookeeperAsyncStatus.NODE_EXISTS);
                break;
            case -112:
                builder.asyncStatus(ZookeeperAsyncStatus.SESSION_EXPIRED);
                break;
            default:
                break;
        }
        builder.context(curatorEvent.getContext());
        builder.value(this.deserialize(this.serializer, curatorEvent.getData()));
        return builder.build();
    }

    /**
     * close client or other close work help for GC
     */
    @Override
    public void close() {
        operatorCallback = null;
        errorListener = null;
    }

    /**
     * 创建一个节点 附带初始化内容 并递归操作
     *
     * @param s          key 不可为null
     * @param s2         value 可为null
     * @param mode       节点模式 临时还是永久及是否带编号 {@link NodeMode}
     * @param recurse    是否递归操作 无父节点自动创建
     * @param serializer 序列化器
     * @return this
     * @throws ZookeeperException failed
     * @see NodeMode
     */
    @Override
    public ZookeeperWriter<String, Serializable> create(String s, Serializable s2, NodeMode mode,
        boolean recurse, Serializer serializer)
        throws ZookeeperException {
        try {
            Assert.notNull(mode, "NodeMode must not be null");
            if (!this.contains(path0(s))) {
                CreateBuilder builder = curatorFramework.create();
                //create mode
                super.withMode(builder, mode);
                builder.inBackground(this, this.threadPool()).withUnhandledErrorListener(this);
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
     * @return this
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
            curatorFramework.delete().withVersion(version).inBackground(this, this.threadPool())
                .withUnhandledErrorListener(this).forPath(path0(s));
            return this;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * 删除一个节点，强制保证删除 接口是一个保障措施，只要客户端会话有效，那么会在后台持续进行删除操作，直到删除节点成功。
     *
     * @param s key
     * @return this
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
            curatorFramework.delete().guaranteed()
                .inBackground(this, this.threadPool())
                .withUnhandledErrorListener(this).forPath(path0(s));
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
     * @return this
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
            delete.inBackground(this, this.threadPool())
                .withUnhandledErrorListener(this);
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
     * 更新数据节点数据 注意：该接口会返回一个Stat实例
     *
     * @param s  key
     * @param s2 value
     * @return this
     * @throws ZookeeperException failed
     */
    @Override
    public ZookeeperWriter<String, Serializable> update(String s, Serializable s2, Integer version,
        Serializer serializer)
        throws ZookeeperException {
        try {
            Assert.notNull(s2, "data must not be empty");
            if (!this.contains(s)) {
                throw new ZookeeperException(
                    String.format("No Path been Created for update by:%s", s));
            }
            final SetDataBuilder setDataBuilder = curatorFramework.setData();
            if (version != null) {
                setDataBuilder.withVersion(version);
            }
            setDataBuilder.inBackground(this, this.threadPool())
                .withUnhandledErrorListener(this)
                .forPath(path0(s),
                    serializer == null ? this.serializer.serialize(s2) : serializer.serialize(s2));
            return this;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    /**
     * 读取一个节点的数据内容 注意，此方法返的返回值是byte[] --> V
     *
     * @param s          key
     * @param serializer 序列化器
     * @return value if null return empty,may be check is't empty
     * @throws ZookeeperException failed
     */
    @Override
    public Serializable get(String s, Serializer serializer) throws ZookeeperException {
        try {
            if (!contains(s)) {
                throw new ZookeeperException(
                    String.format("No Path been Created for get by:%s", s));
            }
            byte[] data = curatorFramework.getData()
                .inBackground(this, this.threadPool())
                .withUnhandledErrorListener(this)
                .forPath(path0(s));
            return this.deserialize(serializer == null ? this.serializer : serializer, data);
        } catch (KeeperException.NoNodeException e) {
            return "";
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
            final List<String> paths =
                curatorFramework.getChildren()
                    .inBackground(this, this.threadPool())
                    .withUnhandledErrorListener(this).forPath(path0(s));
            if (CollectionUtils.isEmpty(paths)) {
                return new String[0];
            }
            String[] keys = new String[paths.size()];
            return paths.toArray(keys);
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }
}
