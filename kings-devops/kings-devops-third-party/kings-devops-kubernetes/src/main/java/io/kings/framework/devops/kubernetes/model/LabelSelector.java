package io.kings.framework.devops.kubernetes.model;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 标签选择器
 *
 * @author lun.wang
 * @date 2022/2/10 5:52 PM
 * @since v2.3
 */
@Getter
@Setter
@Accessors(fluent = true)
public class LabelSelector {

    private List<Requirement> matchExpressions;
    private Map<String, String> matchLabels;

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class Requirement {

        private String key;
        private String operator;
        private List<String> values;
    }
}
