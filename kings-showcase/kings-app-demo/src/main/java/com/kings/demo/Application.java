package com.kings.demo;

import com.kings.demo.repostory.UserInfo;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@EnableJpaRepositories
@Slf4j
class Application implements InitializingBean, DisposableBean, EnvironmentAware {

  Application(UserInfoService userInfoService) {
    this.userInfoService = userInfoService;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class);
  }

  @GetMapping("/env/{key}")
  public String env(@PathVariable String key) {
    return Optional.ofNullable(environment.getProperty(key)).orElse("");
  }

  @Override
  public void destroy() {
    log.debug("bye bye~");
  }

  @Override
  public void afterPropertiesSet() {
    log.debug("hello app~");
  }

  private Environment environment;

  @Override
  public void setEnvironment(@NonNull Environment environment) {
    this.environment = environment;
  }

  @Getter
  @Setter
  @ToString
  @AllArgsConstructor
  static class Person {

    private String name;
    private int age;
    private String phone;
    private String email;
    private String address;
    private Person husband;
  }

  @GetMapping("hello")
  public Mono<String> hello() {
    return Mono.just("hello webflux");
  }

  @GetMapping("health")
  public Mono<String> health() {
    return Mono.just("UP");
  }

  @GetMapping("index")
  public Mono<Person> index() {
    Person lun = new Person("王伦", 26, "15021261772", "kingslun@163.com", "上海市普陀区曹杨新村", null);
    Person you = new Person("吴优", 3, "15971505417", "wuyou@xinlang.com", "上海市普陀区曹杨二村", lun);
    log.info("Response:{}", you);
    return Mono.just(you);
  }

  private final UserInfoService userInfoService;

  @GetMapping("/user/{id}")
  public Mono<UserInfo> user(@PathVariable BigInteger id) {
    return Mono.just(userInfoService.getById(id).orElseThrow(NoSuchElementException::new));
  }
}
