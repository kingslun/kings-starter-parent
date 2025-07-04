#!/bin/zsh
# For Helm 3
#helm install --namespace kings-env gitlab-runner -f ./config.yml gitlab/gitlab-runner
gitlab-runner register --non-interactive \
    --url http://code.kings.com/ \
    --tag-list deploy \
    --registration-token rnMVZJCFxCtjfQsT2emT \
    --name kings-runner \
    --run-untagged=true \
    --executor shell \
    --builds-dir /data/gitlab-runner \
    --config "~/.gitlab-runner/config.toml" \
    --locked="false"
