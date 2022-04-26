package io.kings.devops.backend.ci.auto;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtils {

    public static ScheduledExecutorService schedulePool(int corePoolSize, String threadName) {
        Assert.hasText(threadName, "PoolThread must have name");
        return Executors.newScheduledThreadPool(corePoolSize, r -> new Thread(r, threadName));
    }
}
