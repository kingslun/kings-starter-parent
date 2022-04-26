package io.kings.devops.jenkins;

/**
 * 客户端提供者
 *
 * @author lun.wang
 * @date 2022/3/15 3:05 PM
 * @since v2.4
 */
interface ClientProvider<C> {

    C provide();
}
