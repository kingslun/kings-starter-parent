package io.kings.framework.devops.openapi;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * k8s开放接口
 *
 * @author lun.wang
 * @date 2022/2/23 11:29 AM
 * @since v2.3
 */
@Tag(name = "Kubernetes ApiServer OpenAPI")
@RequestMapping("/v1/kubernetes/")
public interface KubernetesOpenApi {

}
