package com.kings.base.git;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;

public class GitHub {
    private static final String GITLAB_URL = "http://code.aihuishou.com";

    private static final String GITLAB_PRIVATE_TOKEN = "QbkqG2YeoS6kg6EGy_7E";
    private static final GitLabApi gitLabApi =
            new GitLabApi(GitLabApi.ApiVersion.V4, GITLAB_URL, GITLAB_PRIVATE_TOKEN).withRequestTimeout(1000, 1000);

    public static void main(String[] args) {
        try {
            final Branch branch =
                    gitLabApi.getRepositoryApi().getBranch("fusion/ahs-nova", "feature/service-mesh-deploy-type");
            System.out.println(branch);
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
    }
}
