package io.kings.framework.log.desensitize.match;

import io.kings.framework.log.desensitize.DesensitizeContext.DefaultContext;
import io.kings.framework.log.desensitize.DesensitizeException;
import io.kings.framework.log.desensitize.LogContext;
import io.kings.framework.log.desensitize.ObjectUtils;
import io.kings.framework.log.desensitize.match.regular.Regular;
import io.kings.framework.log.desensitize.match.regular.RegularHolder;
import io.kings.framework.log.desensitize.strategy.Strategy;
import java.time.Duration;
import java.util.function.BiConsumer;

/**
 * 脱敏匹配结果抽象类
 *
 * @author lun.wang
 * @date 2022/1/11 3:22 PM
 * @since v1.3
 */
public abstract class AbstractMatchResult implements MatchResult {

    //MatchPair匹配键值对儿 可以通过构造指定其匹配深度 默认64 这可能影响性能？方案？
    protected MatchPair[] pairs;
    protected int pairCount;
    //开始时间
    protected final long startTime;
    //计时器 记录从解析到脱敏完成耗时
    protected Duration timeTakenToReady;

    protected final LogContext context;

    protected AbstractMatchResult(LogContext ctx) {
        if (ctx == null) {
            throw new DesensitizeException("desensitize context is null");
        }
        this.context = ctx;
        this.startTime = System.nanoTime();
        pairs = new MatchPair[ctx.matchDepth()];
    }

    @Override
    public Duration watch() {
        return Duration.ofNanos(System.nanoTime() - this.startTime);
    }

    @Override
    public int groupCount() {
        return this.pairCount;
    }

    @Override
    public MatchPair[] pairs() {
        return this.pairs;
    }

    /**
     * 分析器消费者
     * <br>例如：
     * <table cell-spacing="1">
     * <tr><td>key</td><td>value</td></tr>
     * <tr><td>message</td><td>log mobile_phone:15021261772,chinese_name:张三丰</td></tr>
     * <tr><td>pairs</td><td>[{mobile_phone,18,31},{chinese_name,43,46}]</td></tr>
     * </table>
     *
     * @param consumer 消费者函数
     */
    protected void analyze(BiConsumer<String, Regular> consumer) {
        if (consumer == null) {
            throw new DesensitizeException("Analyze consumer is null");
        }
        if (!this.context.currentValEmpty()) {
            RegularHolder.regulars().forEach((keyword, regular) -> {
                if (ObjectUtils.notEmpty(regular.getAlias())) {
                    for (String alias : regular.getAlias()) {
                        consumer.accept(alias, regular);
                    }
                } else {
                    consumer.accept(keyword, regular);
                }
            });
        }
    }

    //ignore
    @Override
    public final String result() {
        try {
            return this.doResult();
        } catch (Exception ignore) {
            return this.context.currentVal();
        }
    }

    protected abstract String doResult();

    protected String security(Strategy strategy, String origin, boolean stringCached) {
        String security;
        try {
            if (stringCached) {
                security = strategy.sensitive().desensitize(new DefaultContext(origin)).intern();
            } else {
                security = strategy.sensitive().desensitize(new DefaultContext(origin));
            }
        } catch (DesensitizeException e) {
            //replace error msg
            security = "[" + e.getLocalizedMessage() + "]";
        }
        return security;
    }
}
