package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import io.kings.framework.devops.kubernetes.model.enums.DeployStatus;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@EntityScan("io.kings.devops.backend.model")
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KubernetesTest.class)
public class KubernetesTest {

    private final String namespace = "default";
    private final String name = "demo";
    @Autowired
    private KubernetesApiFactory kubernetesApiFactory;
    private PodResource podResource;
    private DeploymentResource deploymentResource;
    private NetworkResource networkResource;

    @Test
    public void serviceStatus() {
        Object status = this.networkResource.svc().withNamespace(namespace).status(name);
        Assertions.assertThat(status).isNotNull();
        System.out.println(status);
    }

    @Before
    public void init() {
        KubernetesApi kubernetesApi = this.kubernetesApiFactory.instance("local");
        Assertions.assertThat(kubernetesApi).isNotNull();
        this.deploymentResource = kubernetesApi.deploymentResource();
        this.podResource = kubernetesApi.podResource();
        networkResource = kubernetesApi.networkResource();
    }

    public void podDelete(String name) throws KubernetesException {
        boolean del = this.podResource.withNamespace(this.namespace).delete(name);
        Assertions.assertThat(del).isTrue();
    }

    public void podLog(String name) throws KubernetesException {
        String log = this.podResource.withNamespace(this.namespace).fetchLog(name, null);
        Assertions.assertThat(log).isNotBlank();
        System.out.printf("#############################Pod:%s#############################", name);
        System.out.println();
        System.out.println(log);
        System.out.println("###############################end##############################");
    }

    @Test
    public void podResourceCurd() throws KubernetesException {
        List<Pod> pods = this.podResource.withNamespace(this.namespace).findByLabel("app=" + name);
        Assertions.assertThat(pods).isNotEmpty();
        System.out.print("###############################begin##############################");
        pods.forEach(System.out::println);
        System.out.print("###############################end##############################");
        Pod pod = pods.get(0);
        podLog(pod.getMetadata().getName());
        podDelete(pod.getMetadata().getName());
    }

    @Test
    public void deploymentGetList() throws KubernetesException {
        List<Deployment> deployments =
            this.deploymentResource.withNamespace(this.namespace).getList("app", name);
        Assertions.assertThat(deployments).isNotEmpty();
        deployments.forEach(System.out::println);
    }

    @Test
    public void deploymentGetOne() throws KubernetesException {
        Deployment deployment = this.deploymentResource.withNamespace(this.namespace)
            .getOne(this.name);
        Assertions.assertThat(deployment).isNotNull();
        System.out.println(deployment);
    }

    @Test
    public void deploymentRestart() throws KubernetesException {
        boolean restart = this.deploymentResource.withNamespace(this.namespace).restart(this.name);
        Assertions.assertThat(restart).isTrue();
    }

    @Test
    public void deploymentRollback() throws KubernetesException {
        String image = "redis:latest";
        boolean rollback = this.deploymentResource.withNamespace(this.namespace)
            .rollback(this.name, image);
        Assertions.assertThat(rollback).isTrue();
    }

    @Test
    public void deploymentConfigYaml() throws KubernetesException {
        String configYaml = this.deploymentResource.withNamespace(this.namespace)
            .getConfigYaml(this.name);
        Assertions.assertThat(configYaml).isNotBlank();
        System.out.println(configYaml);
    }

    @Test
    public void deploymentScale() throws KubernetesException {
        boolean scale = this.deploymentResource.withNamespace(this.namespace).scale(this.name, 1);
        Assertions.assertThat(scale).isTrue();
    }

    @Test
    public void deploymentReplace() throws KubernetesException {
        String config = this.deploymentResource.withNamespace(this.namespace)
            .getConfigYaml(this.name);
        boolean replace = this.deploymentResource.withNamespace(this.namespace)
            .replace(this.name, config);
        Assertions.assertThat(replace).isTrue();
    }

    @Test
    public void deploymentStatus() throws KubernetesException {
        DeployStatus status = this.deploymentResource.withNamespace(this.namespace)
            .getStatus(this.name);
        Assertions.assertThat(status).isNotNull();
        System.out.println(status.getDesc());
    }

    @Test
    public void deploymentDelete() throws KubernetesException {
        //mock delete for all case
        DeploymentResource bak = this.deploymentResource;
        this.deploymentResource = Mockito.mock(DeploymentResource.class);
        Mockito.doReturn(this.deploymentResource).when(this.deploymentResource)
            .withNamespace(this.namespace);
        Mockito.doReturn(true).when(this.deploymentResource).delete(this.name);
        boolean delete = this.deploymentResource.withNamespace(this.namespace).delete(this.name);
        Assertions.assertThat(delete).isTrue();
        this.deploymentResource = bak;
    }

    @Test(expected = KubernetesResourceNotFoundException.class)
    public void kubernetesResourceNotFound() {
        this.deploymentResource.withNamespace("this.namespace").restart("this.name");
    }
}
