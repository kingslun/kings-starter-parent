package io.kings.framework.devops.kubernetes;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.io.IOUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.*;

public class ExecTest {

    //  //apiServer地址和bearer token方式认证
//  private final ApiClient client = Config.fromToken("https://localhost:6443",
//      "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFFa1ZrU2Z0QUtucVZ5U2hZY3laNHRlZUdTQnRrNHU2T2lMYXU0bVRFVEkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJkZWZhdWx0LXRva2VuLWZ6ZHNiIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImRlZmF1bHQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJjMGM0MWI0ZC05MmU2LTRjMzMtOGQwOS0yNTYyNTllOGYzN2YiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06ZGVmYXVsdCJ9.K_bWlj18uNM1_vy2VfsA3hSo37kIQNuqGWyzq-fZ_R-Hc10VwEDNd11ThW5gDMJmMiwd0ySECY8hWEYP5Sn5xefq_HSWob4Ci_OJ_xKjofcHtkNV_qaiuQ8gTB-UCcWUT3nbpJEt4eR7zn07UawlBFk3ahC-wiMYwWWMHAZvPK0nrYPOQvHidAPbpaQmWM1rLnB4W5wMViKDVCFYihFbpDPat_JsCvqa0E-YmFJP9m-WRppWnfFRecRikHzLJua5UfAKKVWUKUxjjCp7U6bsAYaX0oudSX2_CJofg1CMnDOJsO1LKAwn1T_BYsz_f_zHk9I20EumqzsZrvuJTfXOnw",
//      false);
    //apiServer地址和bearer token方式认证
    private final ApiClient client = Config.fromToken("https://114.67.98.82:6443",
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ6ZXVzLWFkbWluLXRva2VuLXE3cnI3Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InpldXMtYWRtaW4iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI0NDRkY2QwZS1jMGM5LTExZWEtOGUxMC1mYTE2M2UzMWUzNDkiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06emV1cy1hZG1pbiJ9.TqkcHwty55XkU6aS8ml5240c6TCkk_ejJJi4hlroQTj3mrbjzXdEgk92dcx4ZMi39Li0UjlUPFXUKRrKrILNDdSIIDnHa4YWm7TGTYVITnX6XzugQdTWJvRMovAejWBA72DVtLR80yRSkaTdIypI7ERQ1ggM8tcy2uUTMdqevslh-9J_khd2T4_VVYRfe-xk_1wvxFD0Kua2o5ny9DanSgVZs9q-syZQsc93pXPFZK3TTVmNtpYAwsm2kSvdtxDxkLF31d2I152OD5E8l9WlS4NFLY_lzeEYvAQzC4c6NU3h7jLVToNEu6njfpfgSYa0cwx138yzLqxkJLiNVMmMtw",
            false);

    @Before
    public void before() {
        Configuration.setDefaultApiClient(client);
    }

    @Test
    public void exec() {
        try {
            String podName = "nova-service-5699d7f56d-64tkt";
            String namespace = "java-service";
            String command = "sh /root/startup/replay/start.sh";
            String res = process(namespace, podName, command);
            Assertions.assertThat(res).isNotBlank();
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 负责读写exec命令执行结果集的单线程池
     */
    private final ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            (r) -> new Thread(r, "KubectlExecutor"));

    private String process(String namespace, String podName, String command)
            throws ExecutionException, InterruptedException, IOException, ApiException {
        Optional<Process> process = Optional.empty();
        try {
            Exec exec = new Exec();
            String[] commands = new String[]{"sh", "-c", command};
            final Process proc = exec.exec(namespace, podName, commands, true, true);
            process = Optional.of(proc);
            Future<Collection<String>> result = executor.submit(
                    () -> IOUtil.readLines(proc.getInputStream()));
            proc.waitFor();
            if (proc.exitValue() != 0) {
                //failure
                System.out.println("failure");
            } else {
                //success
                System.out.println("success");
            }
            StringBuilder ret = new StringBuilder();
            result.get().forEach(line -> ret.append(line).append("\n"));
            return ret.toString();
        } finally {
            process.ifPresent(Process::destroy);
        }
    }
}
