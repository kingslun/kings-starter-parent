package io.kings.devops.jenkins.api;

import io.kings.devops.jenkins.JenkinsException;
import io.kings.devops.jenkins.OffByTwoProvider;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * Jenkins实现
 *
 * @author lun.wang
 * @date 2022/3/15 2:52 PM
 * @since v2.3
 */
@AllArgsConstructor
public class DefaultJob implements Job {

    @NonNull
    private final OffByTwoProvider provider;

    @Override
    public void createJob() {
        try {
            this.provider.provide().createJob(null, null);
        } catch (IOException e) {
            throw new JenkinsException(e.getMessage());
        }
    }
}
