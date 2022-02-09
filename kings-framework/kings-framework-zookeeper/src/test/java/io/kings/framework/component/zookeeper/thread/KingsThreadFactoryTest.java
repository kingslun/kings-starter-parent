package io.kings.framework.component.zookeeper.thread;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class KingsThreadFactoryTest {

    @Test
    public void namedThreadFactory() {
        Assertions.assertThat(KingsThreadFactory.namedThreadFactory("thread-name")).isNotNull();
    }

    @Test
    public void namedThreadFactoryWithIndex() {
        Assertions.assertThat(KingsThreadFactory.namedThreadFactoryWithIndex("thread-name", 1))
            .isNotNull();
    }

    @Test
    public void namedPoolThreadFactoryWithoutNumber() {
        Assertions.assertThat(
                KingsThreadFactory.namedPoolThreadFactoryWithoutNumber("pool-name", "thread-name"))
            .isNotNull();
    }

    @Test
    public void namedPoolThreadFactory() {
        Assertions.assertThat(
                KingsThreadFactory.namedPoolThreadFactory("pool-name", "thread-name"))
            .isNotNull();
    }

    @Test
    public void namedPoolThreadFactoryWithSeparator() {
        Assertions.assertThat(
                KingsThreadFactory.namedPoolThreadFactoryWithSeparator("pool-name", "thread-name", '/'))
            .isNotNull();
    }
}
