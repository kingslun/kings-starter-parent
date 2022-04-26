package io.kings.devops.backend.ci.auto.jenkins;


import io.kings.devops.backend.ci.auto.AutoCiException;
import io.kings.devops.backend.ci.auto.Response;

/**
 * @author lun.wang
 * @date 2022/3/22 3:10 PM
 * @since v2.5
 */
public class JenkinsException extends AutoCiException {

    public JenkinsException(Response.Status status) {
        super(status, (String) null);
    }

    public JenkinsException(Response.Status status, String cause) {
        super(status, cause);
    }
}
