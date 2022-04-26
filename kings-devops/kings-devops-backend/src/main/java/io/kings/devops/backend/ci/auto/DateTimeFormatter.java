package io.kings.devops.backend.ci.auto;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.format.datetime.DateFormatter;

/**
 * 格式化时间
 *
 * @author lun.wang
 * @date 2022/3/25 4:14 PM
 * @since v2.5
 */
@FunctionalInterface
public interface DateTimeFormatter {

    String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String TIMEZONE = "GMT+8";

    String format(Date time);

    DateTimeFormatter DEFAULT = new Default();

    class Default implements DateTimeFormatter {

        Default() {
            formatter = new DateFormatter(DATE_PATTERN);
            formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        }

        private final DateFormatter formatter;

        @Override
        public String format(Date time) {
            if (time == null) {
                return "";
            }
            return formatter.print(time, Locale.CHINESE);
        }
    }
}
