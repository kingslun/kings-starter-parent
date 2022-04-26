package io.kings.devops.backend.ci.auto.sonarqube;

import io.kings.devops.backend.ci.auto.sonarqube.openapi.SonarApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "${sonarqube.host}", name = "SonarApiFeignClient", configuration = SonarApiFeignConfiguration.class)
public interface SonarApiFeignClient extends SonarApi {

    @GetMapping("/health")
    String index();
}
