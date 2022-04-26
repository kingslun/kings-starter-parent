package io.kings.devops.backend.ci.auto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class JenkinsTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(JenkinsTaskApplication.class);
    }
}
