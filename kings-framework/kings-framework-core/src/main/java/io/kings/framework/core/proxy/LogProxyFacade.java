package io.kings.framework.core.proxy;

import io.kings.framework.core.exception.ProxyException;
import io.kings.framework.util.ClassUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * 日志代理
 *
 * @author lun.wang
 * @date 2021/8/11 3:08 下午
 * @since v2.0
 */
@Slf4j
public class LogProxyFacade<D> implements InvocationHandler {

    private final D delegate;

    private LogProxyFacade(D delegate) {
        Assert.notNull(delegate, "delegation is null");
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        try {
            Object result = method.invoke(this.delegate, args);
            if (log.isDebugEnabled()) {
                log.debug("\nOperate success@{}#{} called by:{},result:{}", className, methodName,
                    Arrays.toString(args), result);
            }
            return result;
        } catch (Exception e) {
            Throwable cause = e instanceof InvocationTargetException ?
                ((InvocationTargetException) e).getTargetException() : e;
            if (log.isErrorEnabled()) {
                log.error("\nOperate failure@{}#{} called by:{}", className, methodName,
                    Arrays.toString(args), cause);
            }
            throw new DelegatorExecutionException("Proxy failure", cause);
        }
    }

    @SuppressWarnings("unchecked")
    public static <D> D proxy(D delegate) {
        //获取顶层接口
        Class<?>[] supers = ClassUtils.interfaces(delegate);
        if (supers.length < 1) {
            throw new DelegatorNotImplementAnyInterfaceException("There are no proxy classes");
        }
        return (D) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), supers,
            new LogProxyFacade<>(delegate));
    }

    static class DelegatorExecutionException extends ProxyException {

        public DelegatorExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class DelegatorNotImplementAnyInterfaceException extends ProxyException {

        public DelegatorNotImplementAnyInterfaceException(String message) {
            super(message);
        }
    }
}
