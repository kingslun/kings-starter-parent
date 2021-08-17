package io.kings.framework.data.serializer;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 序列化注解
 *
 * @author lun.wang
 * @date 2021/8/16 4:49 下午
 * @since v2.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnSerializeCondition.class)
public @interface ConditionOnSerialize {
}
