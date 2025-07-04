package io.kings.devops.backend.ci.auto.sonarqube.openapi.dto;

import io.kings.devops.backend.ci.auto.sonarqube.MetricDictionary;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * sonar返回的所有指标
 *
 * @author lun.wang
 * @date 2022/3/30 5:10 PM
 * @since v2.5
 */
@Getter
@Setter
public class MetricKeyResponseDto implements Serializable {

    private List<MetricKey> metrics;
    private Integer total;
    private Integer p;
    private Integer ps;

    /**
     * metrics describe like:
     * <pre>
     * {
     *      "id": "119",
     *      "key": "new_technical_debt",
     *      "type": "WORK_DUR",
     *      "name": "Added Technical Debt",
     *      "description": "Added technical debt",
     *      "domain": "Maintainability",
     *      "direction": -1,
     *      "qualitative": true,
     *      "hidden": false,
     *      "custom": false
     * }
     * </pre>
     */
    @Getter
    @Setter
    public static class MetricKey implements Serializable {

        private String id;
        private String key;
        private String type;
        private String name;
        private String chineseDescribe;
        private String description;
        private String domain;
        private Integer direction;
        private Boolean qualitative;
        private Boolean hidden;
        private Boolean custom;
        private Integer decimalScale;

        public void setKey(String key) {
            this.key = key;
            this.chineseDescribe = MetricDictionary.chinese(key);
        }
    }
}
