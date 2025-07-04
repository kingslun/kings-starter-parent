package com.kings.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
class Application implements InitializingBean, DisposableBean {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


    @Override
    public void destroy() {
        log.debug("bye bye~");
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("hello app~");
    }

    @RestController
    static class Api {
        @GetMapping("/")
        String hello() {
            return "hello";
        }
    }
}
