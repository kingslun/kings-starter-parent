package io.kings.framework.component.zookeeper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 异步事件 remove some filed
 *
 * @author lun.wang
 * @date 2020/4/24 5:23 下午
 * @see org.apache.curator.framework.api.CuratorEvent
 * @since v2.7.2
 */
@Getter
@Setter
@ToString
@Builder
public class ZookeeperAsyncResponse {

    /**
     * 异步类型
     */
    private ZookeeperAsyncType asyncType;

    /**
     * 异步状态
     */
    private ZookeeperAsyncStatus asyncStatus;

    /**
     * 异步操作key
     */
    private String key;

    /**
     * ??
     */
    private Object context;

    /**
     * ??
     */
    private Serializable value;

    /**
     * ??
     */
    private String name;

    /**
     * 异步操作当前节点的子节点
     */
    private List<String> children;
}
