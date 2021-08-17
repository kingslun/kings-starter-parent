package com.king.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lunkings
 */
@SpringBootApplication
@EnableWebSecurity
@RestController
public class Server extends WebSecurityConfigurerAdapter {
    @Configuration
    @EnableAuthorizationServer
    static class AuthServer extends AuthorizationServerConfigurerAdapter {
        @Resource
        private BCryptPasswordEncoder passwordEncoder;

        @Override
        public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
            configurer.inMemory()
                    .withClient("auth-client")
                    .secret(passwordEncoder.encode("secret"))
                    .authorizedGrantTypes("app")
                    .redirectUris("http://localhost:8080/hello");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Server.class);
    }

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder.encode("123456"))
                .roles("admin");
    }
}
