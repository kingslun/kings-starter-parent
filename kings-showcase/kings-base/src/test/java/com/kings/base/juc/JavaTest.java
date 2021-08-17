package com.kings.base.juc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class JavaTest {
    @Test
    void equals() {
        char[] data = new char[1024];
        String one = new String(data);
        String two = new String(data);
        boolean equals = one.equals(two);
        Assertions.assertThat(equals).isTrue();
    }
}
