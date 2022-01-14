package io.kings.framework.log.desensitize;

import io.kings.framework.log.desensitize.strategy.Strategy;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志脱敏定义注解
 * <br>常作用于dto属性之上
 *
 * @author lun.wang
 * @date 2021/12/20 11:24 AM
 * @since v1.1
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Desensitize {

    Class<? extends IogCondition> condition() default IogCondition.class;

    /**
     * 必须要指定脱敏策略
     *
     * @return Strategy
     */
    Strategy strategy();
}
