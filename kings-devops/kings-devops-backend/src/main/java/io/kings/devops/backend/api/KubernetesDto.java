package io.kings.devops.backend.api;

import io.kings.devops.backend.model.KubernetesDo;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * 云描述对象
 *
 * @author lun.wang
 * @date 2022/2/15 2:09 PM
 * @since v2.3
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KubernetesDto implements Serializable {

    private String accessUrl;
    private String accessToken;
    private String description;

    public static KubernetesDto of(KubernetesDo kubernetesDo) {
        if (kubernetesDo == null) {
            throw new ConfigNotFoundException("no such this kubernetes config!");
        }
        KubernetesDto dto = new KubernetesDto();
        dto.accessUrl = kubernetesDo.getAccessUrl();
        dto.accessToken = kubernetesDo.getAccessToken();
        dto.description = kubernetesDo.getDescription();
        return dto;
    }

    @Override
    public String toString() {
        return "Kubernetes{accessUrl='" + this.accessUrl + "', accessToken='"
            + this.tokenDesensitization() + "',description='" + this.description + "'}";
    }

    private String tokenDesensitization() {
        if (StringUtils.hasText(this.accessToken)) {
            int len = this.accessToken.length();
            if (len > 10) {
                return "" + at(0) + at(1) + at(2) + at(3) + at(4)
                    + "***" + at(len - 5) + at(len - 4) + at(len - 3)
                    + at(len - 2) + at(len - 1);
            }
        }
        return this.accessToken;
    }

    private char at(int index) {
        assert this.accessToken != null;
        return this.accessToken.charAt(index);
    }
}
