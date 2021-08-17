package com.kings.base;


import lombok.Getter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = RefreshScopeTest.class)
@Configuration
@RunWith(SpringRunner.class)
class RefreshScopeTest {
    static class MyEvent extends ApplicationEvent {
        @Getter
        private final String name;

        public MyEvent(Object source, String name) {
            super(source);
            this.name = name;
        }

        @Override
        public String toString() {
            return "MyEvent{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Configuration
    static class RefreshScopeBean {
        @Value("spring.application.name")
        private String name;
        @Autowired
        private ApplicationContext ctx;

        @EventListener
        public String event(MyEvent event) {
            System.out.println("deploy:" + event);
            return this.name;
        }

        public void refresh() {
            ctx.publishEvent(new MyEvent(this, "deploy"));
        }
    }

    @Autowired
    private RefreshScopeBean bean;

    @Test
    void print() {
        Assertions.assertThat(bean).isNotNull();
        System.setProperty("spring.application.name", "new");
        bean.refresh();
    }
}
