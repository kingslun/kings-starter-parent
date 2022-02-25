package io.kings.framework.component.zookeeper.thread;

import io.kings.framework.util.thread.ThreadFactory;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ThreadFactoryTest {

    @Test
    public void namedThreadFactory() {
        Assertions.assertThat(ThreadFactory.namedThreadFactory("thread-name")).isNotNull();
    }

    @Test
    public void namedThreadFactoryWithIndex() {
        Assertions.assertThat(ThreadFactory.namedThreadFactoryWithIndex("thread-name", 1))
            .isNotNull();
    }

    @Test
    public void namedPoolThreadFactoryWithoutNumber() {
        Assertions.assertThat(
                ThreadFactory.namedPoolThreadFactoryWithoutNumber("pool-name", "thread-name"))
            .isNotNull();
    }

    @Test
    public void namedPoolThreadFactory() {
        Assertions.assertThat(
                ThreadFactory.namedPoolThreadFactory("pool-name", "thread-name"))
            .isNotNull();
    }

    @Test
    public void namedPoolThreadFactoryWithSeparator() {
        Assertions.assertThat(
                ThreadFactory.namedPoolThreadFactoryWithSeparator("pool-name", "thread-name", '/'))
            .isNotNull();
    }
}
