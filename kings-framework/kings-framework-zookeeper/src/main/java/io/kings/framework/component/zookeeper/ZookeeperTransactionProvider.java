package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import io.kings.framework.component.zookeeper.exception.ZookeeperTransactionException;
import io.kings.framework.data.serializer.Serializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <p>事物操作实现</p>
 *
 * @author lun.wang
 * @date 2020/4/23 4:24 下午
 * @since v2.7.2
 */
class ZookeeperTransactionProvider extends AbstractZookeeper<Serializable>
        implements ZookeeperTransaction<String, Serializable> {

    private static final String NO_PATH = "No Path been Created for update";

    /**
     * 事物操作集合
     */
    private final List<CuratorOp> transactionOperators;

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

    ZookeeperTransactionProvider(CuratorFramework client, ExecutorService threadPool,
                                 Serializer serializer) {
        super(client, threadPool);
        this.serializer = serializer;
        transactionOperators = new ArrayList<>();
    }

    /**
     * convert operator type
     *
     * @param operationType operate type
     * @return really type
     */
    private ZookeeperTransactionType operationType(OperationType operationType) {
        switch (operationType) {
            case CHECK:
                return ZookeeperTransactionType.EXISTS;
            case CREATE:
                return ZookeeperTransactionType.CREATE;
            case DELETE:
                return ZookeeperTransactionType.DELETE;
            case SET_DATA:
                return ZookeeperTransactionType.UPDATE;
            default:
                return null;
        }
    }

    /**
     * help for GC
     */
    @Override
    public void close() {
        this.transactionOperators.clear();
    }

    /**
     * after operate return result collection
     *
     * @return collection
     * @throws ZookeeperTransactionException commit failed
     */
    @Override
    public Collection<ZookeeperTransactionResponse> commit() throws ZookeeperTransactionException {
        try {
            if (!CollectionUtils.isEmpty(this.transactionOperators)) {
                final Collection<CuratorTransactionResult> transactionResults =
                        this.curatorFramework.transaction().forOperations(this.transactionOperators);
                if (CollectionUtils.isEmpty(transactionResults)) {
                    return Collections.emptyList();
                }
                List<ZookeeperTransactionResponse> ret = new ArrayList<>(transactionResults.size());
                transactionResults.forEach(
                        r -> ret.add(new ZookeeperTransactionResponse(operationType(r.getType()),
                                r.getForPath(), r.getResultPath())));
                return ret;
            } else {
                //return empty or answer not operator
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new ZookeeperTransactionException(e);
        }
    }

    /**
     * 创建一个节点 附带初始化内容 并递归操作
     *
     * @param s        key 不可为null
     * @param s2       value 可为null
     * @param nodeMode 节点模式
     * @param recurse  是否递归操作
     * @return this
     * @throws ZookeeperException failed
     * @see NodeMode
     */
    @Override
    public ZookeeperWriter<String, Serializable> create(String s, Serializable s2,
                                                        NodeMode nodeMode,
                                                        boolean recurse, Serializer serializer)
            throws ZookeeperException {
        try {
            final TransactionCreateBuilder<CuratorOp> creator = this.curatorFramework.transactionOp()
                    .create();
            super.withMode(creator, nodeMode);
            final CuratorOp curatorOp = creator.forPath(path0(s),
                    serializer == null ? this.serializer.serialize(s2) : serializer.serialize(s2));
            this.transactionOperators.add(curatorOp);
            return this;
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
                throw new ZookeeperException(NO_PATH);
            }
            final TransactionDeleteBuilder<CuratorOp> deleter = this.curatorFramework.transactionOp()
                    .delete();
            deleter.withVersion(version);
            final CuratorOp curatorOp = deleter.forPath(path0(s));
            this.transactionOperators.add(curatorOp);
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
                throw new ZookeeperException(NO_PATH);
            }
            final TransactionDeleteBuilder<CuratorOp> deleter = this.curatorFramework.transactionOp()
                    .delete();
            final CuratorOp curatorOp = deleter.forPath(path0(s));
            this.transactionOperators.add(curatorOp);
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
        return this.deleteForce(s);
    }

    /**
     * 更新一个节点的数据内容，强制指定版本进行更新
     *
     * @param s       key
     * @param s2      value
     * @param version version
     * @return this
     * @throws ZookeeperException failed
     */
    @Override
    public ZookeeperWriter<String, Serializable> update(String s, Serializable s2, Integer version,
                                                        Serializer serializer)
            throws ZookeeperException {
        try {
            if (!this.contains(s)) {
                throw new ZookeeperException(NO_PATH);
            }
            final TransactionSetDataBuilder<CuratorOp> updater = this.curatorFramework.transactionOp()
                    .setData();
            if (version != null) {
                updater.withVersion(version);
            }
            CuratorOp curatorOp = updater.forPath(path0(s),
                    serializer == null ? this.serializer.serialize(s2) : serializer.serialize(s2));
            this.transactionOperators.add(curatorOp);
            return this;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }
}
