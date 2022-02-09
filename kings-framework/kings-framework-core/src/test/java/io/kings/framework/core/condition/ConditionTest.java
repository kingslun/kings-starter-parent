package io.kings.framework.core.condition;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ConditionTest {

    private static class Switch extends AbstractPropertyCondition {

        @Override
        public boolean match() {
            return super.off("key1");
        }
    }

    private static class NotBlank extends AbstractPropertyCondition {

        @Override
        public boolean match() {
            return super.notBlank("key1");
        }
    }

    @Test
    public void run() {
        Assertions.assertThat(new Switch()).isNotNull();
    }
}
