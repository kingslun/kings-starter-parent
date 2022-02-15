package io.kings.devops.backend;

import io.kings.devops.backend.api.EnvironmentConfigApi;
import io.kings.devops.backend.api.EnvironmentDto;
import io.kings.devops.backend.api.KubernetesConfigApi;
import io.kings.devops.backend.api.KubernetesDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnvironmentConfigApiTest.class)
public class EnvironmentConfigApiTest {

    @Autowired
    private EnvironmentConfigApi environmentApi;
    @Autowired
    private KubernetesConfigApi kubernetesApi;

    @Test
    public void getEnvByCode() {
        EnvironmentDto local = this.environmentApi.findByCode("local");
        Assertions.assertThat(local).isNotNull();
        System.out.println(local);
    }

    @Test
    public void getK8sConfigByEnvCode() {
        KubernetesDto local = this.kubernetesApi.getByEnvCode("local");
        Assertions.assertThat(local).isNotNull();
        System.out.println(local);
    }
}
