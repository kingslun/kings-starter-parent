package io.kings.framework.devops.kubernetes;

import static io.kings.framework.util.DateFormatUtil.KUBERNETES_TIME_PATTERN;
import static io.kings.framework.util.DateFormatUtil.LOCAL_TIME_PATTERN;

import io.kings.framework.devops.kubernetes.exception.KubernetesTimeFormatException;
import io.kings.framework.util.DateFormatUtil;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * kubernetes资源公共api
 *
 * @author lun.wang
 * @date 2022/2/10 11:22 AM
 * @since v2.3
 */
public interface KubernetesResource {

    SimpleDateFormat KUBERNETES_TIME_PATTERN_FORMATTER = new SimpleDateFormat(
        KUBERNETES_TIME_PATTERN);
    SimpleDateFormat LOCAL_TIME_PATTERN_FORMATTER = new SimpleDateFormat(LOCAL_TIME_PATTERN);

    /**
     * 格式化kubernetes接口返回的时间字符串
     *
     * @param time gmt time
     * @return local time format
     */
    default String format(String time) {
        try {
            Date date = DateFormatUtil.parse(time, KUBERNETES_TIME_PATTERN_FORMATTER);
            return DateFormatUtil.format(date, LOCAL_TIME_PATTERN_FORMATTER);
        } catch (ParseException e) {
            //ignore error
            throw new KubernetesTimeFormatException(e.getLocalizedMessage());
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    class Params<S extends Params<S>> implements Serializable {

        @NonNull
        private String namespace;
        @NonNull
        private String name;
        @Nullable
        private Map<String, String> labels;

        @SuppressWarnings("unchecked")
        public S namespace(String namespace) {
            this.namespace = namespace;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S name(String name) {
            this.name = name;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S labels(Map<String, String> labels) {
            this.labels = labels;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S label(@NonNull String labelKey, @NonNull String labelValue) {
            this.labels = Collections.singletonMap(labelKey, labelValue);
            return (S) this;
        }
    }
}
