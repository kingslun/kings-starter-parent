package io.kings.devops.backend.ci.auto.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import io.kings.devops.backend.ci.auto.ThreadUtils;
import io.kings.devops.backend.ci.auto.config.ConfigurationManager;
import io.kings.devops.backend.ci.auto.config.Git;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@AllArgsConstructor
class LocalJenkinsTaskTrigger implements JenkinsTaskTrigger {

    private final ConfigurationManager configurationManager;
    private final ScheduledExecutorService schedulePool = ThreadUtils.schedulePool(
            Runtime.getRuntime().availableProcessors() / 2, "JenkinsTaskTrigger");

    @Override
    public void trigger(@NonNull TriggerContext context) {
        try {
            Assert.notNull(context, "TriggerContext is null!");
            JenkinsServer jenkinsServer = configurationManager.jenkinsServer(context.getEnv());
            JobWithDetails job = jenkinsServer.getJob(context.getJobName());
            int buildNo = job.getNextBuildNumber();
            job.build(buildParams(context));
            if (StringUtils.hasText(context.getTriggerDescribe())) {
                this.submit(jenkinsServer, context.getJobName(), buildNo,
                        context.getTriggerDescribe());
            }
            log.debug("Jenkins task triggered by:{}", context);
        } catch (Exception e) {
            log.warn("Jenkins task:[{}] triggered failure,", context, e);
        }
    }

    private void submit(JenkinsServer jenkins, String jobName, int jobNo, String describe) {
        this.schedulePool.schedule(() -> {
            try {
                JobWithDetails job = jenkins.getJob(jobName);
                if (job == null) {
                    log.warn("Jenkins task named:{} lost", jobName);
                    return;
                }
                Build build = job.getBuildByNumber(jobNo);
                if (build != null) {
                    build.details().updateDescription(describe);
                    log.debug("Successfully set the jenkins task operator information");
                } else {
                    this.submit(jenkins, jobName, jobNo, describe);
                }
            } catch (IOException ignore) {
                log.warn("Failed to setup Jenkins operator information");
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    private Map<String, String> buildParams(TriggerContext context) {
        //build trigger params
        Map<String, String> params = new HashMap<>(3);
        if (StringUtils.hasText(context.getGit())) {
            params.put("git", context.getGit());
            params.put("credentialsId", context.getCredentialsId());
        } else {
            Git git = configurationManager.git(context.getAppName());
            params.put("git", git.url());
            params.put("credentialsId", git.credentialsId());
        }
        params.put("branch", context.getBranch());
        return params;
    }
}
