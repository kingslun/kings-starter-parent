package io.kings.framework.devops.kubernetes.model;

import io.kings.framework.devops.kubernetes.model.enums.Kind;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * kubernetes描述对象
 *
 * @author lun.wang
 * @date 2022/2/10 4:08 PM
 * @since v2.3
 */
@Getter
@Setter
@Accessors(fluent = true)
public abstract class KubernetesObject {

    protected String apiVersion;
    protected Kind kind;
    protected Metadata metadata;
    protected Spec spec;

    @Override
    public String toString() {
        return "KubernetesObject{" +
                "apiVersion='" + apiVersion + '\'' +
                ", kind=" + kind +
                ", metadata=" + metadata +
                ", spec=" + spec +
                '}';
    }
}
