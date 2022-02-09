package io.kings.framework.core.condition;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 根据配置装配
 *
 * @author lun.wang
 * @date 2021/8/10 6:32 下午
 * @since v1.0
 */
@Slf4j
public abstract class AbstractPropertyCondition
    extends AbstractBeanCondition
    implements PropertyCondition {

    /**
     * 配置了 key
     *
     * @param key property key
     * @return true/false
     *///prevent override
    @Override
    public final boolean notBlank(String key) {
        Assert.hasText(key, "Condition notBlank must have an key");
        return StringUtils.hasText(this.environment.getProperty(key));
    }

    //prevent override
    @Override
    public final boolean expectation(String key, String expect) {
        return this.expectation(key, expect, false);
    }

    /**
     * 配置了 key 且值为expect
     *
     * @param key        property key
     * @param expect     期望的值
     * @param ignoreCase 是否忽略大小写
     * @return true/false
     */
    @Override
    public final boolean expectation(String key, String expect, boolean ignoreCase) {
        Assert.hasText(key, "Condition expectedly must have an key");
        Assert.hasText(expect, "Condition expectedly must have an expect val");
        String val = this.environment.getProperty(key);
        return StringUtils.hasText(val) && ignoreCase ? val.equalsIgnoreCase(expect)
            : Objects.equals(val, expect);
    }

    //prevent override
    @Override
    public final boolean on(String key) {
        return this.on(key, true);
    }

    //prevent override
    @Override
    public final boolean off(String key) {
        return this.off(key, true);
    }

    //prevent override
    @Override
    public final boolean on(String key, boolean matchIfMissing) {
        Assert.hasText(key, "Condition on must have key");
        String val = this.environment.getProperty(key);
        boolean no = val == null;
        boolean eq = !no && (Objects.equals(val, "on") || Objects.equals(val, "true"));
        if (matchIfMissing) {
            //可接受未配置
            return no || eq;
        }
        return eq;
    }

    //prevent override
    @Override
    public final boolean off(String key, boolean matchIfMissing) {
        Assert.hasText(key, "Condition off must have key");
        String val = this.environment.getProperty(key);
        boolean no = val == null;
        boolean eq = !no && (Objects.equals(val, "off") || Objects.equals(val, "false"));
        if (matchIfMissing) {
            //可接受未配置
            return no || eq;
        }
        return eq;
    }
}
