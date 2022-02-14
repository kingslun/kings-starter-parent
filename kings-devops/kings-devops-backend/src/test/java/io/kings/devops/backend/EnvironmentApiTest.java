package io.kings.devops.backend;

import io.kings.devops.backend.api.EnvironmentApi;
import io.kings.devops.backend.api.EnvironmentDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

@EnableJpaRepositories
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnvironmentApiTest.class)
public class EnvironmentApiTest {

    @Autowired
    private EnvironmentApi environmentApi;

    @Test
    public void getByCode() {
        EnvironmentDto local = this.environmentApi.getByCode("local");
        Assertions.assertThat(local).isNotNull();
        System.out.println(local);
    }
}
