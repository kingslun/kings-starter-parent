package io.kings.framework.core.bean;

import io.kings.framework.core.exception.BeanLifecycleException;
import lombok.extern.slf4j.Slf4j;

/**
 * bean生命周期抽象类
 *
 * @author lun.wang
 * @date 2021/8/10 3:25 下午
 * @since v2.0
 */
@Slf4j()
public abstract class AbstractBeanLifecycle implements BeanLifecycle {

    @Override
    public void complete() throws BeanLifecycleException {
        if (log.isDebugEnabled()) {
            log.debug("Bean initialization complete");
        }
        BeanLifecycle.super.complete();
    }

    @Override
    public final void afterPropertiesSet() throws BeanLifecycleException {
        BeanLifecycle.super.afterPropertiesSet();
    }

    @Override
    public void destroy() throws BeanLifecycleException {
        if (log.isDebugEnabled()) {
            log.debug("Bean been destroying...");
        }
        BeanLifecycle.super.destroy();
    }
}
