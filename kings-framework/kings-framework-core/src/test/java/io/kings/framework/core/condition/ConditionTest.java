package io.kings.framework.core.condition;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ConditionTest {
    private static class Ignore extends PropertyCondition {
        @Override
        protected boolean matches() {
            return super.off("key1") && super.off("key2", false) ||
                    super.on("key3") || super.on("key4", false);
        }

        @Override
        protected String onMismatch() {
            return "Ignore missing";
        }

        @Override
        public String onMatch() {
            return "Ignore matched";
        }
    }

    @Test
    public void run() {
        Assertions.assertThat(new Ignore()).isNotNull();
    }
}
