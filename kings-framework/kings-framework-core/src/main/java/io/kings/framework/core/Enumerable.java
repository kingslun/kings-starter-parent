package io.kings.framework.core;

/**
 * 枚举接口用于描述一个枚举实例 常用的字典枚举项包含code和描述信息
 *
 * @author lun.wang
 * @date 2021/8/8 5:10 下午
 * @since v1.0
 */
public interface Enumerable<C, D> {

    /**
     * @return enum code
     */
    C getCode();

    /**
     * @return enum description
     */
    D getDesc();
}
