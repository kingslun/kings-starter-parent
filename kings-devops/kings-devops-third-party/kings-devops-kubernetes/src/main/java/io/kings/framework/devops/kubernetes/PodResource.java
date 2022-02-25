package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

/**
 * pod资源相关api
 *
 * @author lun.wang
 * @date 2021/8/3 4:12 下午
 * @since v2.0
 */
public interface PodResource extends KubernetesResource {

    /**
     * 根据name删除pod
     *
     * @param params 参数
     * @return true/false
     */
    boolean delete(Params params);

    /**
     * 根据标签查询pods
     *
     * @param params 参数
     * @return list of pod
     * @see Pod
     */
    List<Pod> findByLabel(Params params);

    /**
     * 拉取日志
     *
     * @param params 参数
     * @return content of log
     */
    String fetchLog(Params params);

    void console(Params params);

    @Getter
    @Setter
    @Accessors(fluent = true)
    class Params extends KubernetesResource.Params<Params> {

        @Nullable
        String container;
        @Nullable
        transient OutputStream socketOut;
        @Nullable
        transient InputStream socketIn;
    }

    interface Pipeline {

        void onMessage(String message) throws IOException;
    }

    @Slf4j
    class WsListener implements ExecListener {

        public void onError(Throwable e) {
            //nothing do with e
            log.warn("WebSocket occur error", e);
        }

        public void onClose() throws IOException {
            log.warn("WebSocket channel starts to close!");
        }

        @Override
        public final void onClose(int code, String reason) {
            try {
                log.warn("WebSocket channel closed with:[{code:{},reason:{}}]", code, reason);
                this.onClose();
            } catch (IOException e) {
                this.onError(e);
            }
        }

        @Override
        public final void onFailure(Throwable t, Response response) {
            log.warn("WebSocket channel failure body:[{}]", response, t);
            this.onError(t);
        }
    }
}
