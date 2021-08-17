package io.kings.framework.component.zookeeper;

import lombok.Getter;

/**
 * 异步单节点操作状态
 *
 * @author lun.wang
 * @date 2020/4/24 7:22 下午
 * @since v2.7.3
 */
public enum ZookeeperAsyncStatus {
    /**
     * 操作成功
     */
    SUCCESS(0, "SUCCESS"),
    /**
     * 客户端与服务端断开连接
     */
    CONNECTION_LOSS(-4, "CONNECTION_LOSS"),
    /**
     * 节点已经存在
     */
    NODE_EXISTS(-110, "NODE_EXISTS"),
    /**
     * 会话过期
     */
    SESSION_EXPIRED(-112, "SESSION_EXPIRED");

    ZookeeperAsyncStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 状态码
     */
    @Getter
    private final Integer code;

    /**
     * 状态描述
     */
    @Getter
    private final String desc;

    @Override
    public String toString() {
        return this.desc;
    }
}
