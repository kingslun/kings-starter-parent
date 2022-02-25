package io.kings.framework.devops.openapi;

import io.kings.framework.core.bean.BeanLifecycle;
import io.kings.framework.devops.kubernetes.KubernetesApiFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 初始化websocket环境
 *
 * @author lun.wang
 * @date 2022/2/24 3:16 PM
 * @since v2.3
 */
@Configuration
@AllArgsConstructor
public class WebSocketConfig implements BeanLifecycle {

    private final KubernetesApiFactory kubernetesApiFactory;

    @Override
    public void complete() {
        assert kubernetesApiFactory != null;
        KubernetesConsoleWebSocketServer.setKubernetesApiFactory(kubernetesApiFactory);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
