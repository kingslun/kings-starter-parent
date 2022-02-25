package io.kings.framework.devops.openapi;

import io.kings.framework.devops.kubernetes.KubernetesApi;
import io.kings.framework.devops.kubernetes.KubernetesApiFactory;
import io.kings.framework.devops.kubernetes.PodResource.Params;
import io.kings.framework.devops.kubernetes.PodResource.Pipeline;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 容器控制台长连接服务端
 *
 * @author lun.wang
 * @date 2022/2/23 2:44 PM
 * @since v2.3
 */
@Slf4j
@Component
@ServerEndpoint("/v1/kubernetes/pod/console/{env}/{namespace}/{name}")
public class KubernetesConsoleWebSocketServer {

    @Setter
    @Getter
    @Accessors(fluent = true)
    private static final class Key {

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

        private String env;
        private String namespace;
        private String name;
        private String container;


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

    private static final Map<Key, KubernetesSession> SESSIONS = new ConcurrentHashMap<>();
    @Setter
    private static KubernetesApiFactory kubernetesApiFactory;

    static class KubernetesSession extends InputStream implements Pipeline, Closeable {

        ByteBuffer buff;
        boolean closed;

        @Override
        public void onMessage(@NonNull String message) {
            this.buff = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public int read() throws IOException {
            while (!closed) {
                if (buff == null) {
                    return -1;
                }
                if (buff.hasRemaining()) {
                    return buff.getInt();
                } else {
                    buff = null;
                    return -1;
                }
            }
            throw new IOException("Channel already closed!");
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        assert kubernetesApiFactory != null;
        Key key = new Key(session);
        String k = key.key();
        KubernetesSession kubernetesSession = new KubernetesSession();
        log.debug("[{}]上线!", k);
        kubernetesSession.onMessage("欢迎" + k + "加入连接！");
        SESSIONS.put(key, kubernetesSession);
        //open connect
        KubernetesApi kubernetesApi = kubernetesApiFactory.instance(key.env());
        kubernetesApi.podResource().console(
            new Params().namespace(key.namespace).name(key.name).container(key.container)
                .socketIn(kubernetesSession).socketOut(session.getBasicRemote().getSendStream()));
    }

    @OnClose
    public void onClose(Session session) {
        Key key = new Key(session);
        log.debug("{}下线", key);
        KubernetesSession kubernetesSession = SESSIONS.get(key);
        Assert.notNull(kubernetesSession, "KubernetesSession is null");
        kubernetesSession.close();
        SESSIONS.remove(key);
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(Session session, String message) {
        Key key = new Key(session);
        log.debug("[{}]发来一条消息:[{}]", key, message);
        KubernetesSession kubernetesSession = SESSIONS.get(key);
        Assert.notNull(kubernetesSession, "KubernetesSession is null");
    }

    @OnError
    public void onError(Session session, Throwable e) {
        final String key = new Key(session).key();
        log.warn("服务端socket[{}]发生错误", key, e);
    }
}
