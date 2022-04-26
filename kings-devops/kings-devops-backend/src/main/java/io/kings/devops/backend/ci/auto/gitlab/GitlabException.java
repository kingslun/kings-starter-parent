package io.kings.devops.backend.ci.auto.gitlab;


import io.kings.devops.backend.ci.auto.AutoCiException;
import io.kings.devops.backend.ci.auto.Response;

/**
 * gitlab错误
 *
 * @author lun.wang
 * @date 2022/3/25 8:42 PM
 * @since v2.5
 */
public class GitlabException extends AutoCiException {

    public GitlabException(Response.Status status, Throwable cause) {
        super(status, cause);
    }
}
