package io.kings.devops.backend.ci.auto.controller;


import static io.kings.devops.backend.ci.auto.Response.Status.ARGUMENTS_VALID_FAILURE;
import static io.kings.devops.backend.ci.auto.Response.Status.NULL_POINTER;
import static io.kings.devops.backend.ci.auto.Response.Status.UNSUPPORTED;

import feign.FeignException;
import io.kings.devops.backend.ci.auto.AutoCiException;
import io.kings.devops.backend.ci.auto.Response;
import io.kings.devops.backend.ci.auto.Response.Status;
import io.kings.devops.backend.ci.auto.gitlab.GitlabException;
import io.kings.devops.backend.ci.auto.jenkins.JenkinsException;
import io.kings.devops.backend.ci.auto.sonarqube.SonarqubeException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author lun.wang
 * @date 2022/3/24 5:32 PM
 * @since v2.5
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public Response<String> feignException(FeignException exception) {
        log.error("FeignException", exception);
        Throwable cause = exception.getCause();
        if (cause instanceof SocketTimeoutException) {
            return Response.of(Status.FAILURE);
        }
        if (cause instanceof UnknownHostException) {
            return Response.of(Status.FAILURE);
        }
        return Response.of(Status.FAILURE, exception.getLocalizedMessage());
    }

    @ExceptionHandler(AutoCiException.class)
    public Response<String> autoCiException(AutoCiException exception) {
        log.error("AutoCiException", exception);
        return Response.of(exception.getStatus(), exception.getLocalizedMessage());
    }

    @ExceptionHandler(JenkinsException.class)
    public Response<String> jenkinsException(JenkinsException exception) {
        log.error("JenkinsException", exception);
        return Response.of(exception.getStatus(), exception.getLocalizedMessage());
    }

    @ExceptionHandler(SonarqubeException.class)
    public Response<String> sonarqubeException(SonarqubeException exception) {
        log.error("SonarqubeException", exception);
        return Response.of(exception.getStatus(), exception.getLocalizedMessage());
    }

    @ExceptionHandler(GitlabException.class)
    public Response<String> gitlabException(GitlabException exception) {
        log.error("GitlabException", exception);
        if (exception.getCause() instanceof GitLabApiException) {
            GitLabApiException apiException = (GitLabApiException) exception.getCause();
            if (apiException.getHttpStatus() == 404) {
                return Response.of(Status.GITLAB_WEBHOOK_RESOURCE_NOTFOUND);
            }
        }
        return Response.of(exception.getStatus(), exception.getLocalizedMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<String> nullPointerException() {
        return Response.of(NULL_POINTER, null);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public Response<String> unsupportedException() {
        return Response.of(UNSUPPORTED, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> argumentValidException(MethodArgumentNotValidException validException) {
        return Response.of(ARGUMENTS_VALID_FAILURE, validException.getLocalizedMessage());
    }
}
