package io.kings.devops.backend.ci.auto.sonarqube.openapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lun.wang
 * @date 2022/3/30 6:06 PM
 * @since v2.5
 */
@Getter
@Setter
public class MetricResponseDto {

    private Component component;
    private List<Error> errors;

    @Getter
    @Setter
    private static class Error {

        String msg;
    }

    @Getter
    @Setter
    public static class Component {

        private String id;
        private String description;
        @JsonProperty("key")
        private String projectKey;
        @JsonProperty("name")
        private String projectName;
        @JsonProperty("measures")
        private List<Metric> metrics;
    }

    @Getter
    @Setter
    public static class Metric implements Serializable {

        @JsonProperty("metric")
        private String key;
        private String value;
        private Boolean bestValue;
    }
}
