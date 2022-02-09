package io.kings.framework.core.bean;

import io.kings.framework.core.exception.BeanLifecycleException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * bean生命周期定义接口 基于spring bean定义bean的生命周期 目前定义初始化、销毁API两个入口
 *
 * @author lun.wang
 * @date 2021/8/10 3:14 下午
 * @since v3.0
 */
public interface BeanLifecycle extends InitializingBean, DisposableBean {

    /**
     * bean初始化完成的入口
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    default void complete() throws BeanLifecycleException {

    }

    default void afterPropertiesSet() throws BeanLifecycleException {
        this.complete();
    }

    /**
     * bean销毁入口 一般在spring IOC容器shutdown时触发
     */
    default void destroy() throws BeanLifecycleException {

    }
}
