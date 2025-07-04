package io.kings.devops.backend.ci.auto.openapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * gitlab回调请求体
 *
 * @author lun.wang
 * @date 2022/3/29 2:39 PM
 * @since v2.5
 */
@Getter
@Setter
public class WebhookRequestDto implements Serializable {


    @JsonProperty("object_kind")
    private Kind kind;
    private Project project;
    private User user;
    //Push events must have value
    @JsonProperty("ref")
    private String branch;
    //提交人
    @JsonProperty("user_username")
    private String gitUser;
    @JsonProperty("total_commits_count")
    private Integer totalCommitsCount;
    //Merge events must have value
    @JsonProperty("object_attributes")
    private ObjectAttributes objectAttributes;

    //Compatible with complex branches
    public void setBranch(String ref) {
        if (StringUtils.hasText(ref)) {
            this.branch = ref.replace("refs/heads/", "");
        }
    }

    public void setKind(String kind) {
        if (StringUtils.hasText(kind)) {
            this.kind = Kind.of(kind);
        }
    }

    @Getter
    @Setter
    public static class Project implements Serializable {

        private Long id;
        @JsonProperty("path_with_namespace")
        private String pathWithNamespace;

        @Override
        public String toString() {
            return "Project{id=" + id + ", pathWithNamespace='" + pathWithNamespace + "'}";
        }
    }

    @Getter
    @Setter
    public static class User implements Serializable {

        private String name;
        private String username;
        @JsonProperty("avatar_url")
        private String avatarUrl;
    }

    @Getter
    @Setter
    public static class ObjectAttributes implements Serializable {

        @JsonProperty("source_branch")
        private String sourceBranch;
        @JsonProperty("target_branch")
        private String targetBranch;
        private MergeState state;
        private String action;

        public void setState(String state) {
            this.state = MergeState.of(state);
        }

        @Override
        public String toString() {
            return "MergeMediaData{sourceBranch='" + sourceBranch + "', targetBranch='"
                    + targetBranch + "', state=" + state + '}';
        }
    }

    //Future-Scalable Rich Merge Events
    @AllArgsConstructor
    public enum MergeState {
        OPENED("opened"), MERGED("merged"), CLOSED("closed"), OTHER("Stay tuned");
        private final String state;

        static MergeState of(String state) {
            if (StringUtils.hasText(state)) {
                return Arrays.stream(MergeState.values())
                        .filter(i -> Objects.equals(i.state, state)).findFirst().orElse(OTHER);
            }
            return null;
        }
    }

    @AllArgsConstructor
    public enum Kind {
        PUSH("push", "push"),//push
        MERGE_REQUEST("merge_request", "merge request"), //mr
        OTHER("Stay tuned", null);//敬请期待
        private final String eventKind;
        @Getter
        private final String print;

        static Kind of(String kind) {
            if (StringUtils.hasText(kind)) {
                return Arrays.stream(Kind.values()).filter(i -> Objects.equals(i.eventKind, kind))
                        .findFirst().orElse(OTHER);
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "GitlabWebhookRequest{" + "kind=" + kind + ", project=" + project + ", branch='"
                + branch + "', totalCommitsCount=" + totalCommitsCount + ", mergeMediaData="
                + objectAttributes + '}';
    }
}
