package io.kings.framework.log.desensitize;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import lombok.Setter;

import java.util.Objects;

/**
 * logback日志格式编译器
 *
 * @author lun.wang
 * @date 2021/12/29 3:36 PM
 * @since v1.1
 */
public class PatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

    public static final String LOG_DESENSITIZE = "LOG_DESENSITIZE";
    public static final String LOG_DESENSITIZE_UNDEFINED = "LOG_DESENSITIZE_IS_UNDEFINED";
    public static final String LOG_DESENSITIZE_DEPTH = "LOG_DESENSITIZE_DEPTH";
    public static final String LOG_DESENSITIZE_DEPTH_UNDEFINED = "LOG_DESENSITIZE_DEPTH_IS_UNDEFINED";
    public static final String LOG_DESENSITIZE_MAXLENGTH = "LOG_DESENSITIZE_MAXLENGTH";
    public static final String LOG_DESENSITIZE_MAXLENGTH_UNDEFINED = "LOG_DESENSITIZE_MAXLENGTH_IS_UNDEFINED";
    public static final String LOG_DESENSITIZE_MATCH_TYPE = "LOG_DESENSITIZE_MATCH_TYPE";
    public static final String LOG_DESENSITIZE_MATCH_TYPE_UNDEFINED = "LOG_DESENSITIZE_MATCH_TYPE_IS_UNDEFINED";
    public static final String LOG_DESENSITIZE_MATCH_REGULAR = "LOG_DESENSITIZE_MATCH_REGULAR";
    public static final String LOG_DESENSITIZE_MATCH_REGULAR_UNDEFINED = "LOG_DESENSITIZE_MATCH_REGULAR_IS_UNDEFINED";
    public static final String LOG_DESENSITIZE_MATCH_REGULAR_END_WITH = "LOG_DESENSITIZE_MATCH_REGULAR_END_WITH";
    public static final String LOG_DESENSITIZE_MATCH_REGULAR_END_WITH_UNDEFINED = "LOG_DESENSITIZE_MATCH_REGULAR_END_WITH_IS_UNDEFINED";
    //字符串缓存池intern开关
    public static final String LOG_DESENSITIZE_USE_STRING_INTERN = "LOG_DESENSITIZE_USE_STRING_INTERN";
    public static final String LOG_DESENSITIZE_USE_STRING_INTERN_UNDEFINED = "LOG_DESENSITIZE_USE_STRING_INTERN_IS_UNDEFINED";
    @Setter
    //日志脱敏开关
    private String desensitize;
    @Setter
    //字符串常量池
    private String useIntern;
    @Setter
    //匹配深度(若深度值过大影响性能)
    private String depth;
    @Setter
    //单条消息的最大长度
    private String maxLength;
    @Setter
    //匹配方式
    private String matchType;
    /**
     * 匹配规则 如何让keyword&regexp通用？
     * <br>keyword格式: PASSWORD:password,pwd|MOBILE_PHONE:mobile,phone,mobile_phone
     */
    @Setter
    private String regular;
    @Setter
    private String regularEndWith;

    @Override
    public void start() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        //cached logback config in the system process
        //compatible property not configured
        // eg:LOG_DESENSITIZE_MATCH_TYPE_IS_UNDEFINED
        if (effectivelyConfigured(this.matchType, LOG_DESENSITIZE_MATCH_TYPE_UNDEFINED)) {
            System.setProperty(LOG_DESENSITIZE_MATCH_TYPE, this.matchType);
        }
        if (effectivelyConfigured(this.regular, LOG_DESENSITIZE_MATCH_REGULAR_UNDEFINED)) {
            System.setProperty(LOG_DESENSITIZE_MATCH_REGULAR, this.regular);
        }
        if (effectivelyConfigured(this.regularEndWith,
                LOG_DESENSITIZE_MATCH_REGULAR_END_WITH_UNDEFINED)) {
            System.setProperty(LOG_DESENSITIZE_MATCH_REGULAR_END_WITH, this.regularEndWith);
        }
        if (effectivelyConfigured(this.desensitize, LOG_DESENSITIZE_UNDEFINED)) {
            this.checkBoolean(this.desensitize);
            System.setProperty(LOG_DESENSITIZE, this.desensitize);
        }
        if (effectivelyConfigured(this.useIntern,
                LOG_DESENSITIZE_USE_STRING_INTERN_UNDEFINED)) {
            this.checkBoolean(this.useIntern);
            System.setProperty(LOG_DESENSITIZE_USE_STRING_INTERN, this.useIntern);
        }
        if (effectivelyConfigured(this.depth, LOG_DESENSITIZE_DEPTH_UNDEFINED)) {
            this.checkInteger(this.depth);
            System.setProperty(LOG_DESENSITIZE_DEPTH, this.depth);
        }
        if (effectivelyConfigured(this.maxLength, LOG_DESENSITIZE_MAXLENGTH_UNDEFINED)) {
            this.checkInteger(this.maxLength);
            System.setProperty(LOG_DESENSITIZE_MAXLENGTH, this.maxLength);
        }
        //call AbstractLogConverter
        patternLayout.start();
        this.layout = patternLayout;
    }

    private void checkBoolean(String bool) {
        if (!("true".equalsIgnoreCase(bool) || "false".equalsIgnoreCase(bool))) {
            throw new io.kings.framework.log.desensitize.DesensitizeException(
                    "Invalid boolean configuration");
        }
    }

    private void checkInteger(String number) {
        if (io.kings.framework.log.desensitize.ObjectUtils.isEmpty(number)) {
            throw new io.kings.framework.log.desensitize.DesensitizeException(
                    "Empty number configuration");
        }
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new io.kings.framework.log.desensitize.DesensitizeException(
                    "Invalid boolean configuration:" + number);
        }
    }

    //check configured property is effectively
    private boolean effectivelyConfigured(String key, String invalidly) {
        return !io.kings.framework.log.desensitize.ObjectUtils.isEmpty(key) && !Objects.equals(
                invalidly, key);
    }
}
