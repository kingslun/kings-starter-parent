package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import io.kings.framework.data.serializer.Serializer;

/**
 * 可轮询操作的函数 遵循fluent风格的服务 因为外界会调用 所以不能为包级私有
 *
 * @author lun.wang
 * @date 2020/4/30 5:37 下午
 * @since v2.7.4
 */
public interface ZookeeperWriter<K, V> {
    /*======================zookeeper轮询操作==========================*/

    /**
     * 创建一个节点，初始内容为空 如果没有设置节点属性，节点创建模式默认为持久化节点，内容默认为空
     *
     * @param k key
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> create(K k) throws ZookeeperException {
        return create(k, null);
    }

    /**
     * 创建一个节点，附带初始化内容
     *
     * @param k key
     * @param v value
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> create(K k, V v) throws ZookeeperException {
        return create(k, v, NodeMode.PERSISTENT, null);
    }

    /**
     * 创建一个节点，附带初始化内容
     *
     * @param k          key
     * @param v          value
     * @param serializer 序列化器
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> create(K k, V v, Serializer serializer)
        throws ZookeeperException {
        return create(k, v, NodeMode.PERSISTENT, serializer);
    }

    /**
     * 创建一个节点，附带初始化内容
     *
     * @param k key
     * @param v value
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> create(K k, V v, NodeMode mode) throws ZookeeperException {
        return create(k, v, mode, null);
    }

    /**
     * 创建一个节点，附带初始化内容
     *
     * @param k          key 不可为null
     * @param v          value 可为null
     * @param serializer 序列化器
     * @param mode       节点模式
     * @return this
     * @throws ZookeeperException failed
     * @see NodeMode
     */
    default ZookeeperWriter<K, V> create(K k, V v, NodeMode mode, Serializer serializer)
        throws ZookeeperException {
        return create(k, v, mode, true, serializer);
    }

    /**
     * 创建一个节点 附带初始化内容 并递归操作
     *
     * @param k          key 不可为null
     * @param v          value 可为null
     * @param serializer 序列化器
     * @param recurse    是否递归操作 无父节点自动创建
     * @param mode       节点模式 临时还是永久及是否带编号 {@link NodeMode}
     * @return this
     * @throws ZookeeperException failed
     * @see NodeMode
     */
    ZookeeperWriter<K, V> create(K k, V v, NodeMode mode, boolean recurse, Serializer serializer)
        throws ZookeeperException;

    /*=================================delete operation===================================*/

    /**
     * 删除一个节点 注意，此方法只能删除叶子节点，否则会抛出异常。
     *
     * @param k key
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> delete(K k) throws ZookeeperException {
        return delete(k, false);
    }

    /**
     * 删除一个节点，强制指定版本进行删除
     *
     * @param k       key
     * @param version version
     * @return this
     * @throws ZookeeperException failed
     */
    ZookeeperWriter<K, V> deleteWithVersion(K k, int version) throws ZookeeperException;

    /**
     * 删除一个节点，强制保证删除 接口是一个保障措施，只要客户端会话有效，那么会在后台持续进行删除操作，直到删除节点成功。
     *
     * @param k key
     * @return this
     * @throws ZookeeperException failed
     */
    ZookeeperWriter<K, V> deleteForce(K k) throws ZookeeperException;

    /**
     * 删除一个节点，并且递归删除其所有的子节点
     *
     * @param k       key
     * @param recurse 是否递归
     * @return this
     * @throws ZookeeperException failed
     */
    ZookeeperWriter<K, V> delete(K k, boolean recurse) throws ZookeeperException;

    /*=================================update operation===================================*/

    /**
     * 更新数据节点数据 注意：该接口会返回一个Stat实例
     *
     * @param k key
     * @param v value
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> update(K k, V v) throws ZookeeperException {
        return this.update(k, v, null, null);
    }

    /**
     * 更新数据节点数据 注意：该接口会返回一个Stat实例
     *
     * @param k          key
     * @param v          value
     * @param serializer 序列化器
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> update(K k, V v, Serializer serializer)
        throws ZookeeperException {
        return this.update(k, v, null, serializer);
    }

    /**
     * 更新一个节点的数据内容，强制指定版本进行更新
     *
     * @param k       key
     * @param v       value
     * @param version version
     * @return this
     * @throws ZookeeperException failed
     */
    default ZookeeperWriter<K, V> update(K k, V v, Integer version) throws ZookeeperException {
        return this.update(k, v, version, null);
    }

    /**
     * 更新一个节点的数据内容，强制指定版本进行更新
     *
     * @param k          key
     * @param v          value
     * @param version    version
     * @param serializer 序列化器
     * @return this
     * @throws ZookeeperException failed
     */
    ZookeeperWriter<K, V> update(K k, V v, Integer version, Serializer serializer)
        throws ZookeeperException;

    /**
     * 注意：该方法返回一个Stat实例，用于检查ZNode是否不存在的操作. 可以调用额外的方法(监控或者后台处理)并在最后调用forPath()指定要操作的ZNode
     *
     * @param k key
     * @return true不存在 or false存在
     * @throws ZookeeperException failed
     */
    boolean contains(K k) throws ZookeeperException;
}
