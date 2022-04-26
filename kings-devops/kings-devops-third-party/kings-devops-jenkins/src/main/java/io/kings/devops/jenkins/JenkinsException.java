package io.kings.devops.jenkins;

/**
 * Jenkins操作异常
 *
 * @author lun.wang
 * @date 2022/3/15 2:54 PM
 * @since v2.4
 */
public class JenkinsException extends RuntimeException {

    public JenkinsException(String message) {
        super(message);
    }
}
