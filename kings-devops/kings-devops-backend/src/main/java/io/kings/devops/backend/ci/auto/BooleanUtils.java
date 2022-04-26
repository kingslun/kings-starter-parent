package io.kings.devops.backend.ci.auto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BooleanUtils {

    public static boolean isTrue(Boolean bol) {
        return bol != null && bol;
    }

    public static boolean isFalse(Boolean bol) {
        return bol == null || !bol;
    }
}
