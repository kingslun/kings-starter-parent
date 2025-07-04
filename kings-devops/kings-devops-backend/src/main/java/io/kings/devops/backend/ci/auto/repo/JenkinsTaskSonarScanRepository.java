package io.kings.devops.backend.ci.auto.repo;

import io.kings.devops.backend.ci.auto.openapi.vo.StaticCodeMetricsQueryRequestVo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@AllArgsConstructor
public class JenkinsTaskSonarScanRepository {

    private final JenkinsTaskSonarScanRepo sonarScanTaskRepo;

    public JenkinsTaskSonarScanDo findByProjectKey(String projectKey) {
        return sonarScanTaskRepo.findByProjectKey(projectKey);
    }

    public void deleteByProjectKey(String projectKey) {
        sonarScanTaskRepo.deleteByProjectKey(projectKey);
    }

    public JenkinsTaskSonarScanDo findByGitlabProjectPathAndBranch(String projectPath,
                                                                   String branch) {
        return sonarScanTaskRepo.findByGitlabProjectPathAndBranch(projectPath, branch);
    }

    public void insert(JenkinsTaskSonarScanDo taskDo) {
        sonarScanTaskRepo.insert(taskDo);
    }

    public void updateById(JenkinsTaskSonarScanDo taskDo) {
        sonarScanTaskRepo.updateById(taskDo);
    }

    public Page<JenkinsTaskSonarScanDo> findAll(StaticCodeMetricsQueryRequestVo requestVo) {
        final int ps = requestVo.getPageSize();
        final int p = requestVo.getPageNumber();
        Pageable pageable = PageRequest.of(p - 1, ps);  //分页信息
        //查询条件构造
        Specification<JenkinsTaskSonarScanDo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>(2);
            predicates.add(cb.equal(root.get("isDelete"), "0"));
            if (StringUtils.hasText(requestVo.getAppName())) {
                predicates.add(cb.equal(root.get("appName"), requestVo.getAppName()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return sonarScanTaskRepo.findAll(spec, pageable);
    }
}
