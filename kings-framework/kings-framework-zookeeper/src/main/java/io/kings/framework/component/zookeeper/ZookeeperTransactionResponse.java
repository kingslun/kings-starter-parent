package io.kings.framework.component.zookeeper;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 事物操作结果对象
 *
 * @author lun.wang
 * @date 2020/4/23 3:37 下午
 * @email lun.wang@zatech.com
 * @since v2.7.2
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ZookeeperTransactionResponse implements Serializable {

    private ZookeeperTransactionType operationType;
    private String forPath;
    private String resultPath;
}
