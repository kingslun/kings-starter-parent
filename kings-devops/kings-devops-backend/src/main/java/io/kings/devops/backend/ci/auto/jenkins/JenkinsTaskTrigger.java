package io.kings.devops.backend.ci.auto.jenkins;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * jenkins任务触发器
 *
 * @author lun.wang
 * @date 2022/4/2 3:58 PM
 * @since v2.5
 */
public interface JenkinsTaskTrigger {

    @Getter
    class TriggerContext {

        private final String env;
        private final String appName;
        private final String jobName;
        private final String branch;

        public TriggerContext(String env, String appName, String jobName, String branch) {
            Assert.hasText(appName, "Trigger appName is required!");
            Assert.hasText(jobName, "Trigger jenkins jobName is required!");
            Assert.hasText(branch, "Trigger branch is required!");
            this.env = env;
            this.appName = appName;
            this.jobName = jobName;
            this.branch = branch;
        }

        //build参数
        private String git;
        private String credentialsId;

        //触发描述
        private String triggerDescribe;

        public TriggerContext withGit(String git, String credentialsId) {
            Assert.hasText(git, "git address is required!");
            Assert.hasText(credentialsId, "git credentialsId is required!");
            this.git = git;
            this.credentialsId = credentialsId;
            return this;
        }

        public TriggerContext withTriggerDescribe(String triggerDescribe) {
            Assert.hasText(triggerDescribe, "TriggerDescribe message is required!");
            this.triggerDescribe = triggerDescribe;
            return this;
        }

        @Override
        public String toString() {
            return "TriggerCtx{env='" + env + "', jobName='" + jobName + "'}";
        }
    }

    //触发jenkins任务
    void trigger(@NonNull TriggerContext context);
}
