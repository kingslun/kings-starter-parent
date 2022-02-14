package io.kings.devops.backend.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 环境信息
 *
 * @author lun.wang
 * @date 2022/2/11 6:39 PM
 * @since v2.3
 */
@Getter
@Setter
@Entity
@ToString
@Table(name = "c_docker_harbor")
public class DockerHarborDo extends BaseDo {

    private String host;
    private String username;
    private String email;
    private String password;
    private String description;
}
