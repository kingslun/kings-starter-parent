package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperTransactionException;

import java.util.Collection;

/**
 * <p>zk事物操作</p>
 *
 * @author lun.wang
 * @date 2020/4/23 3:48 下午
 * @since v2.5.2
 */
public interface ZookeeperTransaction<K, V> extends ZookeeperWriter<K, V>, Zookeeper {

    /**
     * after operate return result collection
     *
     * @return collection
     * @throws ZookeeperTransactionException commit failure
     */
    Collection<ZookeeperTransactionResponse> commit() throws ZookeeperTransactionException;
}
