package io.kings.framework.devops.openapi.exception;

/**
 * 应用模块异常
 *
 * @author lun.wang
 * @date 2021/8/7 3:03 下午
 * @since v1.0
 */
class JenkinsException extends Exception {

    public JenkinsException(String message) {
        super(message);
    }
}
