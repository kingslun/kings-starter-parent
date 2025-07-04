package io.kings.framework.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式化工具类
 *
 * @author lun.wang
 * @date 2022/2/10 6:51 PM
 * @since v2.3
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateFormatUtil {

    public static final String KUBERNETES_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String LOCAL_TIME_PATTERN = "yyyy/MM/dd hh:mm:ss";

    public static Date parse(String time, SimpleDateFormat sdf) throws ParseException {
        return sdf.parse(time);
    }

    public static String format(Date date, SimpleDateFormat sdf) {
        return sdf.format(date);
    }
}
