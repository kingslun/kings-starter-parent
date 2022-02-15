package io.kings.devops.backend.api;

import java.util.NoSuchElementException;

/**
 * 配置不存在
 *
 * @author lun.wang
 * @date 2022/2/15 2:22 PM
 * @since v2.3
 */
public class ConfigNotFoundException extends NoSuchElementException {

    public ConfigNotFoundException(String s) {
        super(s);
    }
}
