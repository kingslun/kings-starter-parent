package io.kings.framework.data.serializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

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
