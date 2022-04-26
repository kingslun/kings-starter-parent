package io.kings.devops.backend.ci.auto.sonarqube;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * 指标描述对象
 * <b></b>
 *
 * @author lun.wang
 * @date 2022/3/31 3:33 PM
 * @since v2.5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricDictionary {

    //Cached because map queries are the fastest,Because this query will be very frequent
    private static final Map<String, String> METRICS_MAP = new HashMap<>(64);

    public static String chinese(String key) {
        return StringUtils.hasText(key) ? METRICS_MAP.get(key) : "";
    }

    static {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("sonar-metrics-dictionary-zh.properties");
            Properties properties = new Properties();
            properties.load(stream);
            properties.forEach((k, v) -> METRICS_MAP.put(String.valueOf(k), String.valueOf(v)));
        } catch (IOException ignore) {
            //nothing
        }
    }
}
