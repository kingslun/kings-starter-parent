package io.kings.framework.core.condition;

import org.springframework.context.annotation.Condition;

/**
 * spring bean自动装配器
 *
 * @author lun.wang
 * @date 2021/12/3 5:01 PM
 * @since v1.0
 */
public interface BeanCondition extends Condition {

    /**
     * 匹配核心入口
     * <br>一切依赖配置文件来匹配的IOC对象都应该实现此接口
     */
    boolean match();

    /**
     * 未匹配成功时输出的话
     * <pre>
     *   eg:
     *    WebSocketServletAutoConfiguration:
     *       Did not match:
     *          - @ConditionalOnClass did not find required class 'javax.websocket.server.ServerContainer' (OnClassCondition)
     * </pre>
     */
    default String onMismatch() {
        return "missed match";
    }

    /**
     * 匹配成功时做的事情
     * <pre>
     *   eg:
     *   AopAutoConfiguration matched:
     *       - @ConditionalOnProperty (spring.aop.auto=true) matched (OnPropertyCondition)
     * </pre>
     */
    default String onMatched() {
        return "matched success";
    }
}
