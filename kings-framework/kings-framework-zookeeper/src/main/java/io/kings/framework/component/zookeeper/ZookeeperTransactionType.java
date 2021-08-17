package io.kings.framework.component.zookeeper;

/**
 * 事物操作类型
 *
 * @author lun.wang
 * @date 2020/4/23 3:38 下午
 * @since v2.7.2
 */
enum ZookeeperTransactionType {
    /**
     * 创建
     */
    CREATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 判断
     */
    EXISTS
}
