package io.kings.framework.core.bean;

/**
 * 可命名的
 *
 * @author lun.wang
 * @date 2021/8/10 4:05 下午
 * @since v2.0
 */
public interface BeanNameDefinition {

    /**
     * 获取bean name
     *
     * @return spring bean name
     */
    default String getBeanName() {
        return getClass().getSimpleName();
    }
}
