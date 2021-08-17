package io.kings.framework.devops.kubernetes.watch;

import org.springframework.beans.factory.Aware;

interface NamingAware extends Aware {
    String name();
}
