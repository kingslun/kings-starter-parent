package io.kings.framework.devops.openapi.controller;

import io.kings.framework.devops.openapi.KubernetesOpenApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * k8s restful接口
 *
 * @author lun.wang
 * @date 2022/2/23 2:06 PM
 * @since v2.3
 */
@RestController
public class KubernetesController implements KubernetesOpenApi {

    @GetMapping("/health")
    public String healthCheck() {
        return "Up";
    }
}
