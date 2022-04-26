package com.kings.demo;

import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@SpringBootApplication
class Application implements InitializingBean, DisposableBean {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void destroy() {
        log.debug("下机下机");
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("你好");
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

    @GetMapping("out")
    public String out() {
        return "v2";
    }

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("in")
    public String call() {
        return restTemplate.getForObject("http://app.kings-ns.svc.cluster.local:8080/out", String.class);
    }

    @GetMapping("health")
    public String health() {
        return "UP";
    }

    @GetMapping("index")
    public Person index() {
        Person lun = new Person("王伦", 26, "15021261772", "kingslun@163.com", "上海市普陀区曹杨新村", null);
        Person you = new Person("吴优", 3, "15971505417", "wuyou@xinlang.com", "上海市普陀区曹杨二村", lun);
        log.info("Response:{}", you);
        return you;
    }
}
