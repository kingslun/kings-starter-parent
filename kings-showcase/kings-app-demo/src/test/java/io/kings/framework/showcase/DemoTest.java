package io.kings.framework.showcase;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@SpringBootTest(classes = {DemoTest.class, DemoTest.DefaultService.class})
@RunWith(SpringRunner.class)
public class DemoTest {

  @Autowired
  private Service service;

  @Test
  public void exec() {
    //Arrange 1:not empty params 2:empty params
    String arg1 = "param";
    //Act
    String exec = this.service.exec(arg1);
    //Assert
    Assertions.assertThat(exec).isNotEmpty();
    //覆盖待测方法的所有分支
    String exec2 = this.service.exec(null);
    Assertions.assertThat(exec2).isEmpty();
  }

  interface Service {

    String exec(String words);
  }

  static class DefaultService implements Service {

    @Override
    public String exec(String words) {
      if (StringUtils.hasText(words)) {
        return Optional.of(words).map(w -> "hello:" + words).get();
      }
      //return empty
      return "";
    }
  }
}
