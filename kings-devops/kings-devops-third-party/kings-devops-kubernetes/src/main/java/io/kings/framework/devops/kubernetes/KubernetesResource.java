package io.kings.framework.devops.kubernetes;

import static io.kings.framework.util.DateFormatUtil.KUBERNETES_TIME_PATTERN;
import static io.kings.framework.util.DateFormatUtil.LOCAL_TIME_PATTERN;

import io.kings.framework.devops.kubernetes.exception.KubernetesTimeFormatException;
import io.kings.framework.util.DateFormatUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * kubernetes资源公共api
 *
 * @param <S> subclass
 * @author lun.wang
 * @date 2022/2/10 11:22 AM
 * @since v2.3
 */
public interface KubernetesResource<S extends KubernetesResource<S>> {

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

    String NAMESPACE_METHOD = "withNamespace";

    /**
     * fluent accessor
     *
     * @param namespace kube ns
     * @return this
     */
    S withNamespace(String namespace);
}
