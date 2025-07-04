package io.kings.framework.devops.openapi.websocket;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * kubernetes pod primary key
 *
 * @author lun.wang
 * @date 2022/2/25 3:20 PM
 * @since v2.3
 */
@Setter
@Getter
@Accessors(fluent = true)
final class Key {

    private String key(String env, String ns, String name, String container) {
        Assert.hasText(env, "KubernetesConsoleKey must had a environment");
        Assert.hasText(ns, "KubernetesConsoleKey must had a namespace");
        Assert.hasText(name, "KubernetesConsoleKey must had a pod name");
        return env + "-" + ns + "-" + name + (StringUtils.hasText(container) ? "-" + container
                : "");
    }

    String key() {
        return key(env, namespace, name, container);
    }

    String env;
    String namespace;
    String name;
    String container;


    Key(@NonNull Session session) {
        Map<String, List<String>> params = session.getRequestParameterMap();
        assert params != null;
        this.env = toString(params.get("env"));
        this.namespace = toString(params.get("namespace"));
        this.name = toString(params.get("name"));
        this.container = toString(params.get("container"));
    }

    @SuppressWarnings("unchecked")
    static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof List) {
            return ((List<String>) obj).get(0);
        }
        return obj.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(env, namespace, name, container);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Key)) {
            return false;
        }
        Key t = (Key) obj;
        return Objects.equals(env, t.env) && Objects.equals(namespace, t.namespace)
                && Objects.equals(name, t.name) && Objects.equals(container, t.container);
    }

    @Override
    public String toString() {
        return this.key();
    }
}