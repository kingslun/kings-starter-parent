package io.kings.framework.log.desensitize;

/**
 * 脱敏异常
 *
 * @author lun.wang
 * @date 2021/12/20 3:00 PM
 * @since v1.1
 */
public class DesensitizeException extends RuntimeException {

    public DesensitizeException(String message) {
        super(message);
    }
}
