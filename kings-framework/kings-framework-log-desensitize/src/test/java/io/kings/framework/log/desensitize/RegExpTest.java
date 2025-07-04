package io.kings.framework.log.desensitize;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpTest {

    @Test(expected = IllegalStateException.class)
    public void test() {
        Pattern pattern = Pattern.compile(
                "(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}");
        String src = "mobile_phone=15021261772,chinese_name=张三丰";
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
        throw new IllegalStateException();
    }
}
