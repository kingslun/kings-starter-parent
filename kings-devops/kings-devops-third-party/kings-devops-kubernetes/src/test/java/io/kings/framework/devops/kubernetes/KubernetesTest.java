package io.kings.framework.devops.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.kings.framework.devops.kubernetes.exception.KubernetesException;
import io.kings.framework.devops.kubernetes.exception.KubernetesResourceNotFoundException;
import io.kings.framework.devops.kubernetes.model.enums.DeployStatus;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KubernetesTest.class)
public class KubernetesTest {

    private final String namespace = "default";
    private final String name = "demo";
    private final Map<String, String> labels = Collections.singletonMap("app", name);
    private final PodResource.Params podParams = new PodResource.Params().namespace(namespace)
            .name(name).labels(labels);
    private final DeploymentResource.Params deploymentParams = new DeploymentResource.Params().namespace(
            namespace).name(name).label("app", name);
    @Autowired
    private KubernetesApiFactory kubernetesApiFactory;
    private PodResource podResource;
    private DeploymentResource deploymentResource;
    private NetworkResource networkResource;

    //==============service case==============
    @Test
    public void serviceStatus() {
        Object status = this.networkResource.svc().status(namespace, name);
        Assertions.assertThat(status).isNotNull();
        System.out.println(status);
    }

    @Before
    public void init() {
        KubernetesApi kubernetesApi = this.kubernetesApiFactory.instance("local");
        Assertions.assertThat(kubernetesApi).isNotNull();
        this.deploymentResource = kubernetesApi.deploymentResource();
        this.podResource = kubernetesApi.podResource();
        this.networkResource = kubernetesApi.networkResource();
    }

    //==============pod case==============
    public void podDelete(String name) {
        boolean del = this.podResource.delete(podParams.name(name));
        Assertions.assertThat(del).isTrue();
    }

    public void podExec(String name) {
        this.podResource.console(podParams.name(name));
    }

    public void podLog(String name) {
        String log = this.podResource.fetchLog(podParams.name(name));
        System.out.printf("####################【Pod:%s log-start】####################", name);
        System.out.println();
        System.out.print(log);
        System.out.println("####################【log-end】####################");
    }

    @Test
    public void podResourceCurd() {
        List<Pod> pods = this.podResource.findByLabel(podParams);
        Assertions.assertThat(pods).isNotEmpty();
        System.out.println("###############【pod-query-start】###############");
        pods.forEach(System.out::println);
        System.out.println("###############【pod-query-end】###############");
        pods.forEach(i -> {
            if (Objects.equals("Running", i.getStatus().getPhase())) {
                podLog(i.getMetadata().getName());
                podExec(i.getMetadata().getName());
                podDelete(i.getMetadata().getName());
            }
        });
    }

    //==============deployment case==============
    @Test
    public void deploymentGetList() {
        List<Deployment> deployments = this.deploymentResource.getList(deploymentParams);
        Assertions.assertThat(deployments).isNotEmpty();
        deployments.forEach(System.out::println);
    }

    @Test
    public void deploymentGetOne() {
        Deployment deployment = this.deploymentResource.getOne(deploymentParams);
        Assertions.assertThat(deployment).isNotNull();
        System.out.println(deployment);
    }

    @Test
    public void deploymentRestart() {
        boolean restart = this.deploymentResource.restart(deploymentParams);
        Assertions.assertThat(restart).isTrue();
    }

    @Test
    public void deploymentRollback() {
        String image = "kingslun/demo:latest";
        boolean rollback = this.deploymentResource.rollback(deploymentParams.image(image));
        Assertions.assertThat(rollback).isTrue();
    }

    @Test
    public void deploymentConfigYaml() {
        String configYaml = this.deploymentResource.getConfigYaml(deploymentParams);
        Assertions.assertThat(configYaml).isNotBlank();
        System.out.println(configYaml);
    }

    @Test
    public void deploymentScale() {
        boolean scale = this.deploymentResource.scale(deploymentParams.replicas(1));
        Assertions.assertThat(scale).isTrue();
    }

    @Test
    public void deploymentReplace() {
        String config = this.deploymentResource.getConfigYaml(deploymentParams);
        boolean replace = this.deploymentResource.replace(deploymentParams.yaml(config));
        Assertions.assertThat(replace).isTrue();
    }

    @Test
    public void deploymentStatus() throws KubernetesException {
        DeployStatus status = this.deploymentResource.getStatus(deploymentParams);
        Assertions.assertThat(status).isNotNull();
        System.out.println(status.getDesc());
    }

    @Test
    public void deploymentDelete() throws KubernetesException {
        //mock delete for all case
        DeploymentResource bak = this.deploymentResource;
        this.deploymentResource = Mockito.mock(DeploymentResource.class);
        Mockito.doReturn(true).when(deploymentResource).delete(deploymentParams);
        boolean delete = this.deploymentResource.delete(deploymentParams);
        Assertions.assertThat(delete).isTrue();
        this.deploymentResource = bak;
    }

    @Test(expected = KubernetesResourceNotFoundException.class)
    public void kubernetesResourceNotFound() {
        this.deploymentResource.restart(deploymentParams.name("notfound"));
    }
}
