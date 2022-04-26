package io.kings.devops.backend.ci.auto;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * 基础异常
 *
 * @author lun.wang
 * @date 2022/4/1 7:01 PM
 * @since v2.5
 */
public class AutoCiException extends RuntimeException {

    @Getter
    @NonNull
    private final Response.Status status;

    public AutoCiException(@NonNull Response.Status status, Throwable cause) {
        super(cause == null ? status.getMsg() : cause.getMessage(), cause);
        this.status = status;
    }

    public AutoCiException(@NonNull Response.Status status, String msg) {
        super(StringUtils.hasText(msg) ? msg : status.getMsg());
        this.status = status;
    }
}
