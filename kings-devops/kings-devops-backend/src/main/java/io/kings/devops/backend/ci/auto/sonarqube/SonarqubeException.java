package io.kings.devops.backend.ci.auto.sonarqube;


import io.kings.devops.backend.ci.auto.AutoCiException;
import io.kings.devops.backend.ci.auto.Response;

/**
 * @author lun.wang
 * @date 2022/3/22 3:10 PM
 * @since v2.5
 */
public class SonarqubeException extends AutoCiException {

    public SonarqubeException(Response.Status status, String msg) {
        super(status, msg);
    }
}
