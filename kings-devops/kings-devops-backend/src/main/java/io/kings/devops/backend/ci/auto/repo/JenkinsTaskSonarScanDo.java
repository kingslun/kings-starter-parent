package io.kings.devops.backend.ci.auto.repo;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * sonar扫描任务实体类
 *
 * @author lun.wang
 * @date 2022/3/22 5:32 PM
 * @since v2.5
 */
@Getter
@Setter
@Entity
@Table(name = "t_jenkins_task_sonar_scan")
public class JenkinsTaskSonarScanDo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    private String env;
    @Column(name = "project_key")
    private String projectKey;
    @Column(name = "app_name")
    private String appName;
    private String branch;
    private String description;
    private String type;
    @Column(name = "start_time")
    private Date startTime;
    private String cron;
    @Column(name = "gitlab_url")
    private String gitlabUrl;
    @Column(name = "gitlab_jenkins_credentials_id")
    private String gitlabJenkinsCredentialsId;
    @Column(name = "gitlab_project_path")
    private String gitlabProjectPath;
    @Column(name = "root_pom_path")
    private String rootPomPath;
    @Column(name = "sonar_host")
    private String sonarHost;
    @Column(name = "sonar_login")
    private String sonarLogin;
    @Column(name = "create_time")
    private Date createTime;
    private BigInteger creator;
    @Column(name = "modify_time")
    private Date modifyTime;
    private BigInteger modifier;
    @Column(name = "is_delete")
    private String isDelete;
    @Column(name = "temp_field1")
    private String tempField1;
    @Column(name = "temp_field2")
    private String tempField2;
    @Column(name = "temp_field3")
    private String tempField3;

    @Override
    public String toString() {
        return "Task{projectKey='" + projectKey + "', type=" + type + "}";
    }

    @AllArgsConstructor
    public enum DeleteState {
        YES("1"), NO("0");
        @Getter
        private final String state;

        public static DeleteState of(String state) {
            return Arrays.stream(DeleteState.values()).filter(i -> Objects.equals(i.state, state))
                    .findFirst().orElseThrow(NoSuchElementException::new);
        }
    }
}
