package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import io.kings.framework.devops.kubernetes.model.Deployment;
import io.kings.framework.devops.kubernetes.model.Pod;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KubernetesTest.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(DefaultKubernetesApi.class)
public class KubernetesTest {

    @Autowired
    private KubernetesApi<KubernetesClient> kubernetesApi;
    /**
     * 本地默认客户端
     */
    private static final Supplier<KubernetesClient> SUPPLIER = () -> {
        final String master = "https://localhost:6443/";
        String command = "kubectl -n kube-system describe secret default| awk '$1==\"token:\"{print $2}'";
        try (InputStream in = Runtime.getRuntime().exec(command).getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            final String token = new String(out.toByteArray(), StandardCharsets.UTF_8);
            final boolean trustCerts = true;
            Config config =
                new ConfigBuilder().withMasterUrl(master).withOauthToken(token)
                    .withTrustCerts(trustCerts).build();
            return new DefaultKubernetesClient(config);//使用默认的就足够了
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };
    private PodResource podResource;
    private DeploymentResource deploymentResource;

    @Before
    public void init() {
        this.podResource = this.kubernetesApi.podResource(1L, SUPPLIER);
        this.deploymentResource = this.kubernetesApi.deploymentResource(1L, SUPPLIER);
        Assertions.assertThat(this.podResource).isNotNull();
        Assertions.assertThat(this.deploymentResource).isNotNull();
    }

    private final String namespace = "kings-env";
    private final String name = "redis";

    public void podDelete(String name) throws KubernetesException {
        boolean del = this.podResource.namespace(this.namespace).delete(name);
        Assertions.assertThat(del).isTrue();
    }

    public void podLog(String name) throws KubernetesException {
        String log = this.podResource.namespace(this.namespace).fetchLog(name, null);
        Assertions.assertThat(log).isNotBlank();
        System.out.printf("#############################Pod:%s#############################", name);
        System.out.println();
        System.out.println(log);
        System.out.println("###############################end##############################");
    }

    @Test
    public void podResourceCurd() throws KubernetesException {
        List<Pod> pods = this.podResource.namespace(this.namespace).findByLabel("app=redis");
        Assertions.assertThat(pods).isNotEmpty();
        System.out.print("###############################begin##############################");
        pods.forEach(System.out::println);
        System.out.print("###############################end##############################");
        Pod pod = pods.get(0);
        podLog(pod.getPodName());
        podDelete(pod.getPodName());
    }

    @Test
    public void deploymentGetList() throws KubernetesException {
        String labelKey = "app";
        String labelValue = "redis";
        List<Deployment> deployments =
            this.deploymentResource.namespace(this.namespace).getList(labelKey, labelValue);
        Assertions.assertThat(deployments).isNotEmpty();
        deployments.forEach(System.out::println);
    }

    @Test
    public void deploymentGetOne() throws KubernetesException {
        Deployment deployment = this.deploymentResource.namespace(this.namespace).getOne(this.name);
        Assertions.assertThat(deployment).isNotNull();
        System.out.println(deployment);
    }

    @Test
    public void deploymentRestart() throws KubernetesException {
        boolean restart = this.deploymentResource.namespace(this.namespace).restart(this.name);
        Assertions.assertThat(restart).isTrue();
    }

    @Test
    public void deploymentRollback() throws KubernetesException {
        String image = "redis:latest";
        boolean rollback = this.deploymentResource.namespace(this.namespace)
            .rollback(this.name, image);
        Assertions.assertThat(rollback).isTrue();
    }

    @Test
    public void deploymentConfigYaml() throws KubernetesException {
        String configYaml = this.deploymentResource.namespace(this.namespace)
            .getConfigYaml(this.name);
        Assertions.assertThat(configYaml).isNotBlank();
        System.out.println(configYaml);
    }

    @Test
    public void deploymentScale() throws KubernetesException {
        boolean scale = this.deploymentResource.namespace(this.namespace).scale(this.name, 1);
        Assertions.assertThat(scale).isTrue();
    }

    @Test
    public void deploymentReplace() throws KubernetesException {
        String config = this.deploymentResource.namespace(this.namespace).getConfigYaml(this.name);
        boolean replace = this.deploymentResource.namespace(this.namespace)
            .replace(this.name, config);
        Assertions.assertThat(replace).isTrue();
    }

    @Test
    public void deploymentStatus() throws KubernetesException {
        Deployment.Status status = this.deploymentResource.namespace(this.namespace)
            .getStatus(this.name);
        Assertions.assertThat(status).isNotNull();
        System.out.println(status);
    }

    @Test
    public void deploymentDelete() throws KubernetesException {
        //mock delete for all case
        DeploymentResource bak = this.deploymentResource;
        this.deploymentResource = Mockito.mock(DeploymentResource.class);
        Mockito.doReturn(this.deploymentResource).when(this.deploymentResource)
            .namespace(this.namespace);
        Mockito.doReturn(true).when(this.deploymentResource).delete(this.name);
        boolean delete = this.deploymentResource.namespace(this.namespace).delete(this.name);
        Assertions.assertThat(delete).isTrue();
        this.deploymentResource = bak;
    }

    @Test(expected = KubernetesResourceNotFoundException.class)
    public void kubernetesResourceNotFound() {
        this.deploymentResource.namespace("this.namespace").restart("this.name");
    }
}
