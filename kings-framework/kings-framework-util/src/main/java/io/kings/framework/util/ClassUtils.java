package io.kings.framework.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * class反射操作工具
 *
 * @author lun.wang
 * @date 2021/8/11 5:46 下午
 * @since v2.0
 */
public class ClassUtils {
    private ClassUtils() {
        throw new IllegalAccessError("not support");
    }

    public static Class<?>[] interfaces(Object instance) {
        return ClassUtils.interfaces(instance.getClass());
    }

    public static Class<?>[] interfaces(Class<?> sourceClass) {
        //获取顶层接口
        Class<?>[] dest = sourceClass.getInterfaces();
        if (dest.length < 1) {
            return new Class[0];
        }
        Set<Class<?>> interfaces = new LinkedHashSet<>(Arrays.asList(dest));
        for (Class<?> clazz : dest) {
            if (clazz.getInterfaces().length > 0) {
                interfaces.addAll(Arrays.asList(ClassUtils.interfaces(clazz)));
            }
        }
        return interfaces.toArray(new Class[]{});
    }
}
