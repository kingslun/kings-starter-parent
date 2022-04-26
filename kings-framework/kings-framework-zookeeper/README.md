### the description of distribute implementation for zookeeper

```text
包装curator 屏蔽其内部对zookeeper节点操作部分的实现以及分布式选举方案的某种实现
开发者无需关注curator 更无须关注zookeeper 因为虽然curator对zookeeper进行了大量的封装
但是某些API依然臃肿 赶紧来使用我们的组件来轻松实现zookeeper客户端操作吧

本文对此组件涉及的部分进行详细介绍 并对其使用做出讲解和demo 帮助用户快速入手 其实使用超级easy
 下面来带你进入zookeeper的世界吧
```

#### menu for this plugin

- the common operator
- the watcher
- the transaction
- the asynchronous

```java
/**
*以下配置文件参考此对象的注释 对每个属性的做又做出了详细描述 开发态可直接在idea本地配置文件中被提示告知
*/
```

#### 简单配置 以property为例 也可以使用yaml 配置属性描述参考上述备注

```properties
#zk服务地址多个以逗号隔开 参考配置对象的注释 以下所有属性都可参考配置对象的备注
octopus.zookeeper.host=localhost:2181
octopus.zookeeper.namespace=namespace
octopus.zookeeper.retry-type=RETRY_ONE_TIME
octopus.zookeeper.retry.sleepMsBetweenRetries=100
octopus.zookeeper.leader-election.election=false
octopus.zookeeper.leader-election.path=leader
octopus.zookeeper.listen-connect-state=false
logging.config=classpath:logback-spring.xml
logging.level.local.tets=DEBUG
```

#### 代码案例

```java
/**
 *用户关注的东西 -直接在你使用的地方依赖 此对象 
 目前key为zookeeper的节点路径（path）value为zookeeper节点存储的数据值 当前版本实现都为String类型
 */
package local.tets;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@SpringBootTest(classes = LocalDemoApplicationTests.class)
@RunWith(SpringRunner.class)
@Slf4j
@EnableAutoConfiguration
@TestPropertySource("classpath:applicationVo-zk.properties")
public class LocalDemoApplicationTests {

    @Autowired
    private Environment environment;
    @Autowired
    ZookeeperComplex<String, String> zookeeperOperator;

    @Test
    public void zkLeader() {
    }

    @Test
    public void zkOperate() throws OctopusZookeeperException {
        String key = "first/second/third", value = "hello zookeeper 3",
                update = "update for value with zookeeper";
        //exist -> create and get unit
        if (zookeeperOperator.nonexistent(key)) {
            zookeeperOperator.create(key, value);
        } else {
            value = zookeeperOperator.get(key);
        }
        assert zookeeperOperator.get(key).equals(value) : "not that";
        //update
        zookeeperOperator.update(key, update);
        assert zookeeperOperator.get(key).equals(update) : "not that for update";
        //delete
        zookeeperOperator.delete("first", true);
        assert !StringUtils.hasText(zookeeperOperator.get(key)) : "delete failed";
    }

    /**
     * watcher test
     *
     * @throws OctopusZookeeperException failed
     */
    @Test
    public void zkWatcher() throws OctopusZookeeperException {
        String key = "key", value = "hello zookeeper";
        if (zookeeperOperator.nonexistent(key)) {
            zookeeperOperator.create(key, value);
        }
        zookeeperOperator.registerPathWatcher(key, (k, v) ->
                System.out.println(String.format("Changed=====>>>{Path:%s,Data:%s}", k, v))
        );
        //此时上面应该能监听到才对 且打印key和update value
        zookeeperOperator.update(key, "update for zookeeper");
        zookeeperOperator.create(key + "/key2", "key2 value");
        //上面应该任然能监听到 且打印key和空value
        zookeeperOperator.delete(key, true);
    }


    /**
     * watcher2 test
     *
     * @throws OctopusZookeeperException failed
     */
    @Test
    public void zkWatcher2() throws OctopusZookeeperException {
        String key = "key", value = "hello zookeeper";
        if (zookeeperOperator.nonexistent(key)) {
            zookeeperOperator.create(key, value);
        }
        zookeeperOperator.registerPathChildrenWatcher(key,
                new ZookeeperPathChildrenWatcher<String, String>() {
                    @Override
                    public void childAdd(ZookeeperComplex<String, String> operator, String s,
                                         String s2) {
                        System.out.println(String.format(
                                "childAdd=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void childRemove(ZookeeperComplex<String, String> operator, String s,
                                            String s2) {
                        System.out.println(String.format(
                                "childRemove=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void childUpdate(ZookeeperComplex<String, String> operator, String s,
                                            String s2) {
                        System.out.println(String.format(
                                "childUpdate=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void initialized(ZookeeperComplex<String, String> operator, String s,
                                            String s2) {
                        System.out.println(String.format(
                                "initialized=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void connectLost(ZookeeperComplex<String, String> operator, String s,
                                            String s2) {
                        System.out.println(String.format(
                                "connectLost=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void connectSuspended(ZookeeperComplex<String, String> operator,
                                                 String s, String s2) {
                        System.out.println(String.format(
                                "connectSuspended=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void connectReconnect(ZookeeperComplex<String, String> operator,
                                                 String s, String s2) {
                        System.out.println(String.format(
                                "connectReconnect=====>>>{Path:%s,Data:%s}", s, s2));
                    }
                }
        );
        //此时上面应该能监听到才对 且打印key和update value
        zookeeperOperator.update(key, "update for zookeeper");
        zookeeperOperator.create(key + "/key2", "key child value");
        //上面应该任然能监听到 且打印key和空value
        zookeeperOperator.delete(key, true);
    }

    /**
     * watcher3 test
     *
     * @throws OctopusZookeeperException failed
     */
    @Test
    public void zkWatcher3() throws OctopusZookeeperException {
        String key = "key", value = "hello zookeeper";
        if (zookeeperOperator.nonexistent(key)) {
            zookeeperOperator.create(key, value);
        }
        zookeeperOperator.registerPathAndChildrenWatcher(key,
                new ZookeeperPathAndChildrenWatcher<String, String>() {
                    @Override
                    public void pathAdd(ZookeeperComplex<String, String> operator, String s,
                                        String s2) {
                        log.debug("pathAdd=====>>>{Path:{},Data:{}}", s, s2);
                    }

                    @Override
                    public void pathRemove(ZookeeperComplex<String, String> operator, String s,
                                           String s2) {
                        log.debug("pathRemove=====>>>{Path:{},Data:{}}", s, s2);

                    }

                    @Override
                    public void pathUpdate(ZookeeperComplex<String, String> operator, String s,
                                           String s2) {
                        log.debug("pathUpdate=====>>>{Path:{},Data:{}}", s, s2);
                    }

                    @Override
                    public void initialized(ZookeeperComplex<String, String> operator, String s,
                                            String s2) {
                        System.out.println(String.format(
                                "initialized=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void connectLost(ZookeeperComplex<String, String> operator, String s,
                                            String s2) {
                        System.out.println(String.format(
                                "connectLost=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void connectSuspended(ZookeeperComplex<String, String> operator,
                                                 String s, String s2) {
                        System.out.println(String.format(
                                "connectSuspended=====>>>{Path:%s,Data:%s}", s, s2));
                    }

                    @Override
                    public void connectReconnect(ZookeeperComplex<String, String> operator,
                                                 String s, String s2) {
                        System.out.println(String.format(
                                "connectReconnect=====>>>{Path:%s,Data:%s}", s, s2));
                    }
                }
        );
        //此时上面应该能监听到才对 且打印key和update value
        zookeeperOperator.update(key, "update for zookeeper");
        zookeeperOperator.create(key + "/key2", "key child value");
        //上面应该任然能监听到 且打印key和空value
        zookeeperOperator.delete(key, true);
    }

    /**
     * poll ope
     *
     * @throws OctopusZookeeperException failed
     */
    @Test
    public void zkPollOpe() throws OctopusZookeeperException {
        String key = "key", key1 = "key1", key2 = "key2", value = "value of key", update =
                "update value";
        zookeeperOperator.create(key, value).create(key1).delete(key1).update(key, update).create(
                key2).delete(key2);
    }

    @Test
    public void zkTransactionOpe() throws OctopusZookeeperException {
        String key = "key", key1 = "key1", key2 = "key2", value = "value of key", update =
                "update value";
        zookeeperOperator.inTransaction(o -> {
            try {
                o.create(key1)
                        .create(key, value)
                        .delete(key1)
                        .update(key, update)
                        .create(key2)
                        .update(key2, update);
            } catch (OctopusZookeeperException ignore) {
            }
        }).forEach(System.out::println);
    }

    @Test
    public void zkAsyncOpe() throws Exception {
        String key = "/key", key1 = "/key1", value = "value", update = "update value";
        this.zookeeperOperator.inAsync(action -> {
                    try {
                        action.create(key, value, NodeMode.EPHEMERAL).create(key1,
                                value, NodeMode.EPHEMERAL).update(key, update).delete(key1);
                    } catch (OctopusZookeeperException e) {
                        assert true : "cause error for async operate";
                    }
                }, (a, b) -> System.out.println(String.format("===>>>Thread:%s,Data:%s",
                Thread.currentThread().getName(), b))
                , (a, v) -> System.out.println("===>>>ERROR:" + v)
        );
    }
}
```