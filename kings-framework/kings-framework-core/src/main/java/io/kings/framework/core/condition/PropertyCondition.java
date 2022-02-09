package io.kings.framework.core.condition;

/**
 * <pre>
 * <b>***匹配器***</b>
 * 封装基于配置文件的匹配器
 * 基于spring-condition的spring bean匹配器
 * 一切{@link this#match}入口返回为TRUE的会被装载到IOC容器，FALSE则不会被加载到IOC容器
 * 说明：
 * 提供丰富的匹配规则，建议直接编写类继承{@link AbstractPropertyCondition}
 * 规则:
 *  1.根据开关匹配
 *   eg: spring.io: true 配置为true时加载/false不加载
 *  2.根据key值匹配
 *   eg: spring.io.some-key: some-value 配置值为some-value时加载/其他值均不加载
 * 实现：
 *  1.{@link PropertyCondition#match()} 匹配规则
 *  2.{@link PropertyCondition#onMismatch()} 匹配失败时
 *  3.{@link PropertyCondition#onMatched()} 匹配成功时
 * 辅助api:
 *  1.{@link PropertyCondition#notBlank(String)} 参数key必须配置了某项值
 *  2.{@link PropertyCondition#expectation(String, String)} 参数key必须配置了期望值
 *  3.{@link PropertyCondition#on(String, boolean)} 参数key必须配置的true或on
 *  4.{@link PropertyCondition#off(String, boolean)} 参数key必须配置的false或off
 * </pre>
 *
 * @author lun.wang
 * @date 2021/11/20 12:52 下午
 * @since v1.0
 */
public interface PropertyCondition extends BeanCondition {

    /**
     * 配置了 key且值不为空
     *
     * @param key property key
     * @return true/false
     */
    boolean notBlank(String key);

    /**
     * 配置了 key 且值与预期相同
     *
     * @param key property key
     * @return true/false
     */
    default boolean expectation(String key, String expect) {
        return this.expectation(key, expect, false);
    }

    boolean expectation(String key, String expect, boolean ignore);

    default boolean on(String key) {
        return this.on(key, true);
    }

    default boolean off(String key) {
        return this.off(key, true);
    }

    /**
     * 配置了 key 且value为true/on
     *
     * @param key            property key
     * @param matchIfMissing 无视未配置key的情况 默认无视
     * @return true/false
     */
    boolean on(String key, boolean matchIfMissing);

    /**
     * 配置了 key 且value为false/off
     *
     * @param key            property key
     * @param matchIfMissing 无视未配置key的情况 默认无视
     * @return true/false
     */
    boolean off(String key, boolean matchIfMissing);
}
