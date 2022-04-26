package io.kings.devops.backend.ci.auto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * restful响应体
 *
 * @author lun.wang
 * @date 2022/3/22 2:17 PM
 * @since v2.5
 */
@Getter
@Setter
@ToString
public class Response<T> {

    @Getter
    @AllArgsConstructor
    public enum Status {
        /**
         * 成功
         */
        SUCCESS(20000, "successfully"),
        /**
         * 未知异常的失败
         */
        FAILURE(50000, "failure"),
        /**
         * Jenkins参数不合法
         */
        JENKINS_PARAMS_INVALID(400010, "jenkins params is invalid"),
        /**
         * 手工任务开始时间不合法 或许是因为小于当前时间
         */
        JENKINS_PARAMS_INVALID_START_TIME(400011,
            "The start time must be greater than the current time!"),
        /**
         * 定时任务必须包含cron表达式
         */
        JENKINS_PARAMS_INVALID_CRON(400012, "The cron task must contain an expression!"),
        /**
         * jenkins参数异常 没有git事件
         */
        JENKINS_PARAMS_INVALID_GIT_EVENT(400013,
            "GitLab event must contain an event may be 'push'!"),
        /**
         * Jenkins实时任务执行失败
         */
        JENKINS_SYNC_TASK_EXECUTION_ERROR(400020, "jenkins sync task execution error"), //
        JENKINS_TASK_PATCH_ERROR(400021, "jenkins task creation failed"),//
        JENKINS_TASK_NOTFOUND(40007, "jenkins task notfound"),//
        /**
         * Jenkins延迟任务执行失败
         */
        JENKINS_DELAY_TASK_EXECUTION_ERROR(40003, "jenkins delay task execution error"),
        /**
         * Jenkins任务模板不存在
         */
        JENKINS_TASK_TEMPLATE_NOTFOUND(40004, "Template NotFound"),
        /**
         * Jenkins任务模板初始化出错
         */
        JENKINS_TASK_TEMPLATE_INIT_FAILURE(40005, "init jenkins template failure"),//
        JENKINS_TASK_CONSOLE_LOG_FAILURE(40006, "Jenkins log query failed"),
        /**
         * 空指针异常 这是极其危险的低级错误
         */
        NULL_POINTER(70001, "Operated on null value is a serious programmer error!"),
        /**
         * 参数异常
         */
        ARGUMENTS_VALID_FAILURE(40002, "illegal arguments"),
        /**
         * add webhook错误
         */
        GITLAB_WEBHOOK_CHECK_FAILURE(50001,
            "check contains gitlab webhook error"), GITLAB_WEBHOOK_PATCH_FAILURE(50002,
            "patch gitlab webhook error"), GITLAB_WEBHOOK_DELETE_FAILURE(50003,
            "delete gitlab webhook error"), GITLAB_WEBHOOK_RESOURCE_NOTFOUND(50004,
            "gitlab webhook resource notfound"),
        /**
         * 敬请期待
         */
        UNSUPPORTED(60001, "Unsupported operation,may be coming!"), SONARQUBE_API_CALL_FAIL(70001,
            "Failed to call sonar api");
        private final Integer code;
        private final String msg;
    }

    private Response(T data) {
        this(Status.SUCCESS, data);
    }

    private Response(@NonNull Status status, T data) {
        Assert.notNull(status, "restful response status is null");
        this.code = status.code;
        this.msg = status.msg;
        this.data = data;
    }

    private final Integer code;
    private final String msg;
    private final T data;

    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    public static <T> Response<T> of(@NonNull Status status, T data) {
        return new Response<>(status, data);
    }

    public static <T> Response<T> of(@NonNull Status status) {
        return new Response<>(status, null);
    }
}
