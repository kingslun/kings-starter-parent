package io.kings.devops.backend.ci.auto.sonarqube;

import feign.Request;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import io.kings.devops.backend.ci.auto.Response.Status;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableFeignClients
public class SonarApiFeignConfiguration {

    @Value("${sonarqube.username}")
    private String usm;
    @Value("${sonarqube.password}")
    private String pwd;

    @Bean
    @Primary
    Request.Options options() {
        return new Request.Options(3, TimeUnit.SECONDS, 3, TimeUnit.SECONDS, false);
    }

    @Bean
    @Primary
    ErrorDecoder errorDecoder() {
        return new ErrorDecoder404();
    }

    static class ErrorDecoder404 implements ErrorDecoder {

        private final ErrorDecoder decoder = new ErrorDecoder.Default();

        @Override
        public Exception decode(String s, Response response) {
            if (response.status() == 404) {
                throw new SonarqubeException(Status.SONARQUBE_API_CALL_FAIL,
                        "resource notfound, params:" + response.request().requestTemplate().queries());
            }
            return decoder.decode(s, response);
        }
    }

    @Bean
    @Primary
    BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(usm, pwd);
    }
}
