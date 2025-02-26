package com.kings.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application implements InitializingBean, DisposableBean {

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
}
