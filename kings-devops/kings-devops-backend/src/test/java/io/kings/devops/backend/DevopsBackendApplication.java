package io.kings.devops.backend;

import io.kings.devops.backend.api.EnvironmentApi;
import io.kings.devops.backend.api.EnvironmentDto;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@SpringBootApplication
@EnableJpaRepositories
public class DevopsBackendApplication {

    private final EnvironmentApi environmentApi;

    @GetMapping("env/{code}")
    public EnvironmentDto index(@PathVariable String code) {
        return this.environmentApi.getByCode(code);
    }

    public static void main(String[] args) {
        SpringApplication.run(DevopsBackendApplication.class);
    }
}
