package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author lun.wang
 * @date 2021/12/30 4:56 PM
 * @since v1.2
 */
@Getter
@AllArgsConstructor
public enum Strategy {
    PASSWORD("password", null) {
        @Override
        public DesensitizeApi sensitive() {
            return PASSWORD0;
        }
    }, MOBILE_PHONE("mobile_phone",
            "(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}") {
        @Override
        public DesensitizeApi sensitive() {
            return MOBILE_PHONE0;
        }
    }, EMAIL("email", "[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+") {
        @Override
        public DesensitizeApi sensitive() {
            return EMAIL0;
        }
    }, CHINESE_NAME("chinese_name", "[\\u4E00-\\u9FA5]{2,4}") {
        @Override
        public DesensitizeApi sensitive() {
            return CHINESE_NAME0;
        }
    }, BANK_CARD("bank_card", "([1-9]{1})(\\d{14}|\\d{18})") {
        @Override
        public DesensitizeApi sensitive() {
            return BANK_CARD0;
        }
    }, ID_CARD("id_card",
            "[1-9]\\d{5}(18|19|20|(3\\d))\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]") {
        @Override
        public DesensitizeApi sensitive() {
            return ID_CARD0;
        }
    };
    /**
     * 脱敏策略关键字
     */
    private final String keyword;
    /**
     * 脱敏正则表达式-将对匹配的值进行实施验证
     */
    private final String pattern;

    /**
     * @return ISensitive实现
     */
    public abstract DesensitizeApi sensitive();

    static class NoSuchSensitiveStrategyException extends DesensitizeException {

        NoSuchSensitiveStrategyException() {
            super("NoSuch Sensitive Strategy!");
        }

        NoSuchSensitiveStrategyException(String keyword) {
            super("NoSuch Sensitive Strategy [" + keyword + "]!");
        }
    }

    public static Strategy of(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return Arrays.stream(Strategy.values())
                    .filter(strategy -> keyword.equalsIgnoreCase(strategy.keyword)).findFirst()
                    .orElseThrow(NoSuchSensitiveStrategyException::new);
        }
        throw new NoSuchSensitiveStrategyException(keyword);
    }

    /**
     * all keywords
     *
     * @return ["password","bank_card",...]
     */
    public static String[] keywords() {
        if (keywords == null) {
            Strategy[] strategies = Strategy.values();
            String[] kws = new String[strategies.length];
            for (int i = 0; i < strategies.length; i++) {
                kws[i] = strategies[i].keyword;
            }
            keywords = kws;
        }
        return keywords;
    }

    private static String[] keywords;

    //具体策略实现静态常量
    static final DesensitizeApi BANK_CARD0 = new BankCardDesensitize();
    static final DesensitizeApi CHINESE_NAME0 = new ChineseNameDesensitize();
    static final DesensitizeApi EMAIL0 = new EmailDesensitize();
    static final DesensitizeApi ID_CARD0 = new IDCardDesensitize();
    static final DesensitizeApi MOBILE_PHONE0 = new MobilePhoneDesensitize();
    static final DesensitizeApi PASSWORD0 = new PasswordDesensitize();
}