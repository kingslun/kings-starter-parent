package io.kings.framework.core.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 自动装配加载抽象类
 *
 * @author lun.wang
 * @date 2021/12/3 5:41 PM
 * @since v1.0
 */
public abstract class AbstractBeanCondition extends SpringBootCondition implements BeanCondition {

    /**
     * 环境持有者 只需要environment？
     * <br>你要的它{@link ConditionContext}都有
     */
    protected Environment environment;

    @Override
    public final ConditionOutcome getMatchOutcome(
            ConditionContext ctx, AnnotatedTypeMetadata metadata) {
        this.environment = ctx.getEnvironment();
        return this.match() ? ConditionOutcome.match(this.onMatched())
                : ConditionOutcome.noMatch(onMismatch());
    }
}
