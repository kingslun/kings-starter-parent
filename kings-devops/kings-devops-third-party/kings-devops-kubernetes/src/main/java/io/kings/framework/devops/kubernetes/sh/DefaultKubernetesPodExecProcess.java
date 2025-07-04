package io.kings.framework.devops.kubernetes.sh;

import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * exec api进程处理类
 *
 * @author lun.wang
 * @date 2021/8/23 11:13 上午
 * @since v2.0
 */
@Slf4j
public final class DefaultKubernetesPodExecProcess implements KubernetesPodExecProcess {

    private final Exec exec;
    private final String namespace;
    private final String podName;
    private final String cmd;
    private final String validSh;

    public DefaultKubernetesPodExecProcess(String namespace, String podName, String cmd,
                                           String validSh) {
        Assert.hasText(namespace, "namespace must not be empty");
        Assert.hasText(podName, "podName must not be empty");
        Assert.hasText(cmd, "cmd must not be empty");
        this.namespace = namespace;
        this.podName = podName;
        this.cmd = cmd;
        this.validSh = validSh;
        exec = new Exec();
    }

    /**
     * 负责读写exec命令执行结果集的单线程池
     */
    private static final ExecutorService WORKER;

    static {
        WORKER = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                r -> new Thread(r, "KubectlExecWorker"));
    }

    private Process submit(String cmd) throws IOException, ApiException {
        String[] commands = new String[]{"sh", "-c", cmd};
        return this.exec.exec(this.namespace, this.podName, commands, true, true);
    }

    @Override
    public ExecResponse exec(boolean waitFor, long timeout, TimeUnit unit) {
        Process process = null;
        Process validProc = null;
        ExecResponse response = new ExecResponse();
        try {
            process = this.submit(this.cmd);
            response.setSubmitted(true);
            if (waitFor) {
                Assert.notNull(unit, "TimeUnit is null");
                Assert.isTrue(timeout > 0, "timeout must be great by zero");
                final Process proc = process;
                WORKER.submit(() -> {
                    //读取结果
                    try {
                        response.setSuccessMsg(
                                StreamUtils.copyToString(proc.getInputStream(), StandardCharsets.UTF_8));
                        response.setFailureMsg(
                                StreamUtils.copyToString(proc.getErrorStream(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        throw new KubernetesException(e);
                    }
                });
                proc.waitFor(timeout, unit);
            }
            if (StringUtils.hasText(this.validSh)) {
                validProc = this.submit(this.validSh);
                final Process valid = validProc;
                WORKER.submit(() -> {
                    //读取结果
                    try {
                        response.setValidSuccessMsg(
                                StreamUtils.copyToString(valid.getInputStream(), StandardCharsets.UTF_8));
                        response.setValidFailureMsg(
                                StreamUtils.copyToString(valid.getErrorStream(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        throw new KubernetesException(e);
                    }
                });
            }
            log.debug("Exec command:{} @ ns:{}#pod:{}", this.cmd, this.namespace, this.podName);
            return response;
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                throw new KubernetesResourceNotFoundException();
            }
            log.error("Exec command:{} failure @ ns:{}#pod:{}", this.cmd, this.namespace, this.podName,
                    e);
            throw new KubernetesException(e);
        } catch (IOException e) {
            log.error("Exec command:{} ioe @ ns:{}#pod:{}", this.cmd, this.namespace, this.podName);
            throw new KubernetesException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Exec command:{} interrupt @ ns:{}#pod:{}", this.cmd, this.namespace,
                    this.podName, e);
            throw new KubernetesException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (validProc != null) {
                validProc.destroy();
            }
        }
    }
}
