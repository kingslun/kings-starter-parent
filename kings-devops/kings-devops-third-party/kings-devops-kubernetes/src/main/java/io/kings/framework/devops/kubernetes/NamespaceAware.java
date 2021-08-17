package io.kings.framework.devops.kubernetes;

import org.springframework.beans.factory.Aware;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 节点终结器
 *
 * @param <S> subclass
 * @author lun.wang
 * @date 2021/8/3 7:42 下午
 * @since v2.0
 */
@FunctionalInterface
public interface NamespaceAware<S> extends Aware {
    String METHOD_NAME = "namespace";

    S namespace(String namespace);

    default long convert(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return sdf.parse(time).getTime();
        } catch (ParseException e) {
            //ignore error
            throw new IllegalArgumentException(e);
        }
    }
}
