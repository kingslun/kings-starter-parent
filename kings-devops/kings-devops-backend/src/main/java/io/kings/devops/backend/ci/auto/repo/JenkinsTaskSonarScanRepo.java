package io.kings.devops.backend.ci.auto.repo;

import java.math.BigInteger;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 数据持久层
 *
 * @author lun.wang
 * @date 2022/3/22 5:32 PM
 * @since v2.5
 */
interface JenkinsTaskSonarScanRepo extends CrudRepository<JenkinsTaskSonarScanDo, BigInteger>,
        JpaSpecificationExecutor<JenkinsTaskSonarScanDo> {

    @Query("from JenkinsTaskSonarScanDo where projectKey=:#{#projectKey}")
    JenkinsTaskSonarScanDo findByProjectKey(String projectKey);

    //根据gitlab项目和分支查询任务信息
    @Query("from JenkinsTaskSonarScanDo where gitlabProjectPath=:#{#gitlabProjectPath} and branch=:#{#branch} and isDelete='0'")
    JenkinsTaskSonarScanDo findByGitlabProjectPathAndBranch(String gitlabProjectPath,
                                                            String branch);

    @Modifying
    @Transactional
    @Query(value = "insert into t_jenkins_task_sonar_scan(env, app_name, branch, description, type, project_key, cron, gitlab_url, gitlab_jenkins_credentials_id, gitlab_project_path, root_pom_path, sonar_host, sonar_login) values(:#{#taskDo.env},:#{#taskDo.appName},:#{#taskDo.branch},:#{#taskDo.description},:#{#taskDo.type},:#{#taskDo.projectKey},:#{#taskDo.cron},:#{#taskDo.gitlabUrl},:#{#taskDo.gitlabJenkinsCredentialsId},:#{#taskDo.gitlabProjectPath},:#{#taskDo.rootPomPath},:#{#taskDo.sonarHost},:#{#taskDo.sonarLogin})", nativeQuery = true)
    void insert(JenkinsTaskSonarScanDo taskDo);

    @Modifying
    @Transactional
    @Query(value = "update t_jenkins_task_sonar_scan set is_delete=1 where project_key=:#{#projectKey}", nativeQuery = true)
    void deleteByProjectKey(String projectKey);

    @Modifying
    @Transactional
    @Query(value = "update t_jenkins_task_sonar_scan set description=:#{#taskDo.description}, type=:#{#taskDo.type},  cron=:#{#taskDo.cron}, root_pom_path=:#{#taskDo.rootPomPath}, modify_time=current_timestamp where id=:#{#taskDo.id} and is_delete=0", nativeQuery = true)
    void updateById(JenkinsTaskSonarScanDo taskDo);
}
