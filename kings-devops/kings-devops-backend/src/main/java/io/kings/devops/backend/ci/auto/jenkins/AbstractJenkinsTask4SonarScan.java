package io.kings.devops.backend.ci.auto.jenkins;

import static io.kings.devops.backend.ci.auto.Response.Status.JENKINS_PARAMS_INVALID_CRON;
import static io.kings.devops.backend.ci.auto.Response.Status.JENKINS_PARAMS_INVALID_GIT_EVENT;
import static io.kings.devops.backend.ci.auto.Response.Status.JENKINS_PARAMS_INVALID_START_TIME;
import static io.kings.devops.backend.ci.auto.Response.Status.JENKINS_TASK_PATCH_ERROR;
import static io.kings.devops.backend.ci.auto.Response.Status.JENKINS_TASK_TEMPLATE_INIT_FAILURE;
import static io.kings.devops.backend.ci.auto.Response.Status.JENKINS_TASK_TEMPLATE_NOTFOUND;

import com.offbytwo.jenkins.JenkinsServer;
import io.kings.devops.backend.ci.auto.BooleanUtils;
import io.kings.devops.backend.ci.auto.ProjectKeyGenerator;
import io.kings.devops.backend.ci.auto.config.ConfigurationManager;
import io.kings.devops.backend.ci.auto.config.Git;
import io.kings.devops.backend.ci.auto.config.SonarQube;
import io.kings.devops.backend.ci.auto.gitlab.GitLabWebhookService;
import io.kings.devops.backend.ci.auto.gitlab.WebhookObject;
import io.kings.devops.backend.ci.auto.jenkins.DelayTaskCacheManager.DelayedTaskContext;
import io.kings.devops.backend.ci.auto.openapi.vo.CreateSonarScanTaskRequestVo;
import io.kings.devops.backend.ci.auto.openapi.vo.CreateSonarScanTaskResponseVo;
import io.kings.devops.backend.ci.auto.openapi.vo.GitlabWebhookVo;
import io.kings.devops.backend.ci.auto.openapi.vo.TaskType;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanDo;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanDo.DeleteState;
import io.kings.devops.backend.ci.auto.repo.JenkinsTaskSonarScanRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * 通过Jenkins发布三方jar到nexus
 *
 * @author lun.wang
 * @date 2021/9/1 3:30 下午
 * @since v1.0
 */
@Slf4j
abstract class AbstractJenkinsTask4SonarScan {

    protected AbstractJenkinsTask4SonarScan(@NonNull JenkinsTaskSonarScanRepository repository,
        ConfigurationManager configurationManager, DelayTaskCacheManager delayTaskCacheManager,
        GitLabWebhookService gitLabWebhookService) {
        this.repository = repository;
        this.configurationManager = configurationManager;
        this.delayTaskCacheManager = delayTaskCacheManager;
        this.gitLabWebhookService = gitLabWebhookService;
        projectKeyGenerator = ProjectKeyGenerator.DEFAULT;
    }

    protected final JenkinsTaskSonarScanRepository repository;
    protected final ConfigurationManager configurationManager;
    protected final ProjectKeyGenerator projectKeyGenerator;
    private final DelayTaskCacheManager delayTaskCacheManager;
    private final GitLabWebhookService gitLabWebhookService;
    //jenkins任务模板
    protected static final Document JENKINS_TASK_MAVEN_PROJECT_TEMPLATE;

    //fetch template
    static {
        try {
            InputStream stream = AbstractJenkinsTask4SonarScan.class.getResourceAsStream(
                "/jenkins-task-sonar-scan-template.xml");
            if (stream == null) {
                throw new JenkinsException(JENKINS_TASK_TEMPLATE_NOTFOUND);
            }
            SAXReader reader = new SAXReader();
            JENKINS_TASK_MAVEN_PROJECT_TEMPLATE = reader.read(stream);
        } catch (DocumentException e) {
            throw new JenkinsException(JENKINS_TASK_TEMPLATE_INIT_FAILURE);
        }
    }


    private static final String ROOT_POM_PATH = "pom.xml";
    private static final String VARIABLE_REGEXP = "\\$\\{\\w+}";
    private static final String JENKINS_TIMEZONE = "TZ=Asia/Shanghai\n";

    public final CreateSonarScanTaskResponseVo createStaticScanTask(
        CreateSonarScanTaskRequestVo requestVo) {
        try {
            ExecuteContext context = this.preCreateSonarScanTask(requestVo);
            this.doCreateSonarScanTask(context);
            return this.postCreateSonarScanTask(context);
        } catch (IOException e) {
            throw new JenkinsException(JENKINS_TASK_PATCH_ERROR, "patch jenkins task failure");
        }
    }

    public abstract void deleteStaticScanTask(String projectKey);

    public abstract String latestStaticScanTaskConsoleLog(String projectKey);

    protected abstract void doCreateSonarScanTask(ExecuteContext context) throws IOException;

    //Cancel this manual task if not already executed
    protected void cancelDelayTaskIfPresent(String projectKey) {
        DelayedTaskContext cached = delayTaskCacheManager.cached(projectKey);
        if (cached != null) {
            cached.future.cancel(true);
            delayTaskCacheManager.remove(projectKey);
            log.info(
                "It is no longer necessary to execute the original delayed task {} when creating a auto executable task.now canceled",
                cached.information());
        }
    }

    //clear gitlab webhook
    protected void clearGitlabWebhookIfPresent(WebhookObject webhook) {
        gitLabWebhookService.delete(webhook);
    }

    //resp
    protected CreateSonarScanTaskResponseVo postCreateSonarScanTask(ExecuteContext context) {
        if (TaskType.MANUAL != context.taskType()) {
            //不是手工任务需要取消 延迟任务
            cancelDelayTaskIfPresent(context.projectKey());
        }
        if (TaskType.GITLAB_EVENT != context.taskType()) {
            clearGitlabWebhookIfPresent(context.webhook());
        }
        CreateSonarScanTaskResponseVo responseVo = new CreateSonarScanTaskResponseVo();
        responseVo.setStatus(true);
        responseVo.setInformation("SUBMITTED");
        return responseVo;
    }

    private void validManual(Date startTime) {
        if (startTime != null && startTime.compareTo(new Date()) < 0) {
            throw new JenkinsException(JENKINS_PARAMS_INVALID_START_TIME);
        }
    }

    private void validSchedule(String cron) {
        if (!StringUtils.hasText(cron)) {
            throw new JenkinsException(JENKINS_PARAMS_INVALID_CRON);
        }
    }

    private void validGitlabEvent(GitlabWebhookVo webhookVo) {
        if (webhookVo == null) {
            throw new JenkinsException(JENKINS_PARAMS_INVALID_GIT_EVENT);
        }
        boolean noMr = BooleanUtils.isFalse(webhookVo.getEnableMergeRequestEvents());
        boolean noPush = BooleanUtils.isFalse(webhookVo.getEnablePushEvents());
        if (noPush && noMr) {
            throw new JenkinsException(JENKINS_PARAMS_INVALID_GIT_EVENT);
        }
    }

    //1、参数验证  2、配置Jenkins 3、存储任务
    protected ExecuteContext preCreateSonarScanTask(CreateSonarScanTaskRequestVo requestVo)
        throws IOException {
        //filling default config
        if (!StringUtils.hasText(requestVo.getRootPomPath())) {
            requestVo.setRootPomPath(ROOT_POM_PATH);
        }

        final String projectKey = projectKeyGenerator.projectKey(requestVo.getEnv(),
            requestVo.getAppName(), requestVo.getBranch());
        final JenkinsTaskSonarScanDo before = repository.findByProjectKey(projectKey);
        final JenkinsTaskSonarScanDo now = rebuildDo(requestVo, before);
        now.setProjectKey(projectKey);
        final TaskType taskType = requestVo.getTaskType();
        final JenkinsServer jenkinsServer = this.configurationManager.jenkinsServer(
            requestVo.getEnv());
        //context
        ExecuteContext context = DefaultExecuteContext.from(requestVo);
        //manual valid startTime
        if (TaskType.MANUAL == taskType) {
            this.validManual(requestVo.getStartTime());
        }
        //schedule valid cron
        if (TaskType.SCHEDULE == taskType) {
            this.validSchedule(requestVo.getCron());
        }
        //gitlab valid event
        String projectPath =
            before == null ? now.getGitlabProjectPath() : before.getGitlabProjectPath();
        if (TaskType.GITLAB_EVENT == taskType) {
            this.validGitlabEvent(requestVo.getWebhook());
            boolean push = BooleanUtils.isTrue(requestVo.getWebhook().getEnablePushEvents());
            boolean mr = BooleanUtils.isTrue(requestVo.getWebhook().getEnableMergeRequestEvents());
            boolean ssl = BooleanUtils.isTrue(requestVo.getWebhook().getEnableSslVerification());
            context.setWebhook(new WebhookObject().enablePushEvents(push)
                //push the associated branch
                .pushEventsBranchFilter(requestVo.getBranch()).enableMergeRequestEvents(mr)
                .secretToken(requestVo.getWebhook().getSecretToken()).enableSslVerification(ssl)
                .projectPath(projectPath));
        } else {
            context.setWebhook(new WebhookObject().projectPath(projectPath)
                .pushEventsBranchFilter(requestVo.getBranch()));
        }
        Element root = JENKINS_TASK_MAVEN_PROJECT_TEMPLATE.getRootElement();
        //filling jenkins job description
        if (StringUtils.hasText(requestVo.getTaskDescription())) {
            root.element("description").setText(requestVo.getTaskDescription());
        }
        //可能涉及获取对应环境的sonar信息 测试阶段暂用写死的数据
        root.element("rootPOM").setText(requestVo.getRootPomPath());
        Element goals = root.element("goals");
        //format cmd config variables
        String variablesCmd = goals.getText().trim();
        SonarQube sonarQube = configurationManager.sonarQube(requestVo.getEnv());
        String cmd = variablesCmd.replaceFirst(VARIABLE_REGEXP, requestVo.getRootPomPath())
            .replaceFirst(VARIABLE_REGEXP, sonarQube.host())
            .replaceFirst(VARIABLE_REGEXP, sonarQube.login())
            .replaceFirst(VARIABLE_REGEXP, projectKey).replaceFirst(VARIABLE_REGEXP, projectKey);
        goals.setText(cmd);

        DefaultElement triggers = new DefaultElement("triggers");
        //filling schedule task variable
        if (TaskType.SCHEDULE == taskType) {
            //不需要考虑定时任务的触发 - 须确保定时任务的配置无变量
            Element scm = root.element("scm");
            Git git = configurationManager.git(requestVo.getAppName());
            Element userRemoteConfig = scm.element("userRemoteConfigs")
                .element("hudson.plugins.git.UserRemoteConfig");
            userRemoteConfig.element("url").setText(git.url());
            userRemoteConfig.element("credentialsId").setText(git.credentialsId());
            scm.element("branches").element("hudson.plugins.git.BranchSpec").element("name")
                .setText(requestVo.getBranch());
            //filling cron
            DefaultElement timerTrigger = new DefaultElement("hudson.triggers.TimerTrigger");
            DefaultElement spec = new DefaultElement("spec");
            spec.setText(JENKINS_TIMEZONE + requestVo.getCron());
            timerTrigger.add(spec);
            triggers.add(timerTrigger);
        }
        //不是定时任务 需要去除静态模板中 可能缓存地 上个定时任务配置的 定时器
        root.add(triggers);

        //persist jenkins job
        if (before == null) {
            //persist save
            this.repository.insert(now);
            jenkinsAddJob(jenkinsServer, projectKey);
        } else {//persist merge
            //兼容逻辑删除
            DeleteState deleteState = DeleteState.of(before.getIsDelete());
            switch (deleteState) {
                case YES:
                    jenkinsAddJob(jenkinsServer, projectKey);
                    break;
                case NO:
                    jenkinsPatch(jenkinsServer, projectKey);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            this.repository.updateById(updateDo(before, now));
        }
        return context;
    }

    //build task
    private void jenkinsAddJob(JenkinsServer jenkinsServer, String projectKey) throws IOException {
        jenkinsServer.createJob(projectKey, JENKINS_TASK_MAVEN_PROJECT_TEMPLATE.asXML());
        log.debug("add jenkins task named:{}", projectKey);
    }

    //reconfigure task
    private void jenkinsPatch(JenkinsServer jenkinsServer, String projectKey) throws IOException {
        jenkinsServer.updateJob(projectKey, JENKINS_TASK_MAVEN_PROJECT_TEMPLATE.asXML());
        log.debug("patch jenkins task named:{}", projectKey);
    }

    private JenkinsTaskSonarScanDo updateDo(JenkinsTaskSonarScanDo before,
        JenkinsTaskSonarScanDo now) {
        before.setDescription(now.getDescription());
        before.setCron(now.getCron());
        before.setRootPomPath(now.getRootPomPath());
        before.setType(now.getType());
        before.setEnv(now.getEnv());
        before.setAppName(now.getAppName());
        before.setBranch(now.getBranch());
        before.setIsDelete(DeleteState.NO.getState());
        return before;
    }

    private JenkinsTaskSonarScanDo rebuildDo(CreateSonarScanTaskRequestVo requestVo,
        JenkinsTaskSonarScanDo old) {
        JenkinsTaskSonarScanDo taskDo = new JenkinsTaskSonarScanDo();
        taskDo.setDescription(requestVo.getTaskDescription());
        taskDo.setAppName(requestVo.getAppName());
        taskDo.setBranch(requestVo.getBranch());
        taskDo.setRootPomPath(requestVo.getRootPomPath());
        taskDo.setEnv(requestVo.getEnv());
        taskDo.setType(requestVo.getTaskType().name());
        taskDo.setCron(requestVo.getCron());
        //trigger time
        taskDo.setStartTime(
            requestVo.getStartTime() == null ? new Date() : requestVo.getStartTime());
        taskDo.setIsDelete(DeleteState.NO.getState());
        if (old == null) {
            //filling sonar info
            SonarQube sonarQube = configurationManager.sonarQube(requestVo.getEnv());
            taskDo.setSonarHost(sonarQube.host());
            taskDo.setSonarLogin(sonarQube.login());
            //filling gitlab info
            Git git = configurationManager.git(requestVo.getAppName());
            taskDo.setGitlabUrl(git.url());
            taskDo.setGitlabJenkinsCredentialsId(git.credentialsId());
            taskDo.setGitlabProjectPath(git.projectPath());
        }
        return taskDo;
    }
}
