package io.kings.framework.core.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 根据配置装配
 *
 * @author lun.wang
 * @date 2021/8/10 6:32 下午
 * @since v2.0
 */
@Slf4j
public abstract class PropertyCondition implements Condition {
    private static final String WARNING = "\nThe bean cannot be initialized because of the configuration is incorrect.";
    private Environment environment;

    @Override
    public final boolean matches(@NonNull ConditionContext ctx, @NonNull AnnotatedTypeMetadata metadata) {
        this.environment = ctx.getEnvironment();
        boolean matched = this.matches();
        if (!matched) {
            //warn log
            if (log.isWarnEnabled()) {
                log.warn(this.onMismatch());
            }
            //warn programming? necessity is up to me to determine
        } else {
            if (log.isDebugEnabled()) {
                log.debug(this.onMatch());
            }
        }
        return matched;
    }

    protected abstract boolean matches();

    /**
     * 匹配失败信息
     */
    protected String onMismatch() {
        return WARNING;
    }

    /**
     * 匹配成功信息
     */
    public String onMatch() {
        return "The bean will be conditioned by:" + this.getClass().getSimpleName();
    }

    /**
     * 配置了 key
     *
     * @param key property key
     * @return true/false
     */
    protected final boolean notBlank(String key) {
        Assert.hasText(key, "Condition notBlank must have an key");
        return StringUtils.hasText(this.environment.getProperty(key));
    }

    /**
     * 配置了 key 且值为expect
     *
     * @param key property key
     * @return true/false
     */
    protected final boolean expectedly(String key, String expect) {
        Assert.hasText(key, "Condition expectedly must have an key");
        Assert.hasText(expect, "Condition expectedly must have an expect val");
        String val = this.environment.getProperty(key);
        return StringUtils.hasText(val) && Objects.equals(val, expect);
    }

    protected final boolean on(String key) {
        return this.on(key, true);
    }

    protected final boolean off(String key) {
        return this.off(key, true);
    }

    /**
     * 配置了 key value 为true/on
     *
     * @param key            property key
     * @param matchIfMissing 如果为true则未配置时也满足匹配条件
     * @return true/false
     */
    protected final boolean on(String key, boolean matchIfMissing) {
        Assert.hasText(key, "Condition on must have key");
        String val = this.environment.getProperty(key);
        if (matchIfMissing) {
            return !StringUtils.hasText(val);
        }
        return StringUtils.hasText(val) && (Objects.equals(val, "on") || Objects.equals(val, "true"));
    }

    /**
     * 配置了 key value 为false/off
     *
     * @param key            property key
     * @param matchIfMissing true:key不存在返回true
     * @return true/false
     */
    protected final boolean off(String key, boolean matchIfMissing) {
        Assert.hasText(key, "Condition off must have key");
        String val = this.environment.getProperty(key);
        if (matchIfMissing) {
            return !StringUtils.hasText(val);
        }
        return StringUtils.hasText(val) && (Objects.equals(val, "off") || Objects.equals(val, "false"));
    }
}
