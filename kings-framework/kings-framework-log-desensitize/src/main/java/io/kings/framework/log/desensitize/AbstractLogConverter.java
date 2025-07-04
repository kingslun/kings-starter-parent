package io.kings.framework.log.desensitize;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.DynamicConverter;
import io.kings.framework.log.desensitize.match.LogMatcher;
import io.kings.framework.log.desensitize.match.keyword.KeywordMatcher;
import io.kings.framework.log.desensitize.match.regexp.RegexpMatcher;
import io.kings.framework.log.desensitize.match.regular.Regular;
import io.kings.framework.log.desensitize.match.regular.RegularHolder;
import io.kings.framework.log.desensitize.strategy.Strategy;

import java.util.HashMap;
import java.util.Map;

import static io.kings.framework.log.desensitize.PatternLayoutEncoder.*;

/**
 * 抽象日志转换器
 *
 * @author lun.wang
 * @date 2021/12/29 4:28 PM
 * @since v1.1
 */
abstract class AbstractLogConverter extends DynamicConverter<ILoggingEvent> {

    private boolean desensitize;
    protected boolean useIntern;
    protected int depth;
    protected int maxLength;
    private static final String FIRST_LEVEL_SEPARATOR = "\\|";
    private static final String SECOND_LEVEL_SEPARATOR = ":";
    private static final String THIRD_LEVEL_SEPARATOR = ",";
    /**
     * 默认退出策略 片段计算器 计算 name:张三丰 中张三丰的开始和结束下标 因为规则可能多变因此抽离实现出去 应对多变
     */
    private static final char[] skipped = {',', ')', ' '};
    protected LogMatcher logMatcher;

    /**
     * 解析logback脱敏变量并设置默认值
     */
    @Override
    public void start() {
        this.desensitize = Boolean.parseBoolean(System.getProperty(LOG_DESENSITIZE, "true"));
        this.useIntern = Boolean.parseBoolean(
                System.getProperty(LOG_DESENSITIZE_USE_STRING_INTERN, "false"));
        this.depth = Integer.parseInt(System.getProperty(LOG_DESENSITIZE_DEPTH, "128"));
        this.maxLength = Integer.parseInt(System.getProperty(LOG_DESENSITIZE_MAXLENGTH, "1024"));
        MatchType matchType = MatchType.of(
                System.getProperty(LOG_DESENSITIZE_MATCH_TYPE, "KEYWORD"));
        this.logMatcher = this.logMatcher(matchType);
        //regular
        String regular = System.getProperty(LOG_DESENSITIZE_MATCH_REGULAR);
        char[] endWith = this.endWith();
        Map<String, Regular> regulars = new HashMap<>();
        if (!ObjectUtils.isEmpty(regular)) {
            //regular PASSWORD:password,pwd|MOBILE_PHONE:mobile,phone,mobile_phone
            String[] firsts = regular.split(FIRST_LEVEL_SEPARATOR);
            for (String first : firsts) {
                String[] seconds = first.split(SECOND_LEVEL_SEPARATOR);
                if (seconds.length < 2) {
                    throw new DesensitizeException("illegal regular:" + first);
                }
                Strategy strategy = Strategy.of(seconds[0]);
                regulars.put(strategy.getKeyword(),
                        new Regular(strategy, seconds[1].split(THIRD_LEVEL_SEPARATOR), endWith));
            }
        } else {
            for (String keyword : Strategy.keywords()) {
                Strategy strategy = Strategy.of(keyword);
                regulars.put(keyword, new Regular(strategy, null, endWith));
            }
        }
        RegularHolder.cacheRegular(regulars);
        super.start();
    }

    private LogMatcher logMatcher(MatchType matchType) {
        if (MatchType.KEYWORD == matchType) {
            return new KeywordMatcher();
        }
        if (MatchType.REGEXP == matchType) {
            return new RegexpMatcher();
        }
        throw new DesensitizeException("not support match type");
    }

    private char[] endWith() {
        String regularEndWith = System.getProperty(LOG_DESENSITIZE_MATCH_REGULAR_END_WITH);
        char[] endWith;
        if (!ObjectUtils.isEmpty(regularEndWith)) {
            String[] endWith0 = regularEndWith.split(FIRST_LEVEL_SEPARATOR);
            char[] endWith1 = new char[endWith0.length];
            for (int i = 0; i < endWith0.length; i++) {
                String end = endWith0[i];
                if (end == null || end.length() != 1) {
                    throw new DesensitizeException("illegal endWith:" + end);
                }
                endWith1[i] = end.charAt(0);
            }
            endWith = endWith1;
        } else {
            endWith = skipped;
        }
        return endWith;
    }

    @Override
    public String convert(ILoggingEvent event) {
        final String source = event.getFormattedMessage();
        //关闭脱敏
        if (!this.desensitize) {
            return source;
        }
        //长度不符
        if (source == null || source.length() < 1 || source.length() > maxLength) {
            return source;
        }
        return this.sensitive(source);
    }

    /**
     * 脱敏逻辑实现
     *
     * @param source 源日志
     * @return 脱敏后日志
     */
    abstract String sensitive(String source);
}
