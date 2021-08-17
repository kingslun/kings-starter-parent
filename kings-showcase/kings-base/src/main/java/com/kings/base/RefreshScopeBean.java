package com.kings.base;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class RefreshScopeBean {
    @Value("spring.application.name")
    private String name;

    @EventListener(ApplicationReadyEvent.class)
    public String print() {
        return this.name;
    }
}
