package io.kings.framework.component.zookeeper;

import io.kings.framework.component.zookeeper.exception.ZookeeperException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = KingsZookeeperTest.class)
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
public class KingsZookeeperTest {

    private final String key = "key";
    private final String transKey = "transKey";
    private final String asyncKey = "asyncKey";

    @Before
    public void before() throws ZookeeperException {
        if (!this.zookeeper.contains(this.key)) {
            this.zookeeper.create(this.key, "init");
        }
        if (!this.zookeeper.contains(this.transKey)) {
            this.zookeeper.create(this.transKey, "init");
        }
        if (!this.zookeeper.contains(this.asyncKey)) {
            this.zookeeper.create(this.asyncKey, "init");
        }
    }

    @After
    public void after() throws ZookeeperException {
        if (this.zookeeper.contains(this.key)) {
            this.zookeeper.delete(this.key);
        }
        if (this.zookeeper.contains(this.transKey)) {
            this.zookeeper.delete(this.transKey);
        }
        if (this.zookeeper.contains(this.asyncKey)) {
            this.zookeeper.delete(this.asyncKey);
        }
    }

    @Autowired
    KingsZookeeper zookeeper;

    /**
     * zk 增删改查的用例
     *
     * @throws ZookeeperException failure
     */
    @Test
    public void zookeeperCurd() throws ZookeeperException {
        final String update = "update for key";
        //read
        Assertions.assertThat(this.zookeeper.get(key)).isNotNull().isEqualTo("init");
        //update
        this.zookeeper.update(key, update);
        Assertions.assertThat(this.zookeeper.get(key)).isNotNull().isEqualTo(update);
        //add child for children
        this.zookeeper.create(this.key + "/key2", "key2 value");
        //children api
        Assertions.assertThat(this.zookeeper.children(key)).isNotEmpty();
        //delete
        this.zookeeper.delete(key, true);
    }

    /**
     * watcher test
     */
    @Test
    public void zookeeperWatcher() throws ZookeeperException {
        //watcher
        this.zookeeper.registerPathWatcher(key,
                (k, v) -> System.out.printf("Changed=====>>>{Path:%s,Data:%s}", k, v))
            .update(key, "update for watcher").create(key + "/key2", "key2 value")
            .delete(key + "/key2");
        Assertions.assertThat(this.zookeeper).isNotNull();
    }


    /**
     * watcher2 test
     *
     * @throws ZookeeperException failed
     */
    @Test
    public void zookeeperWatcher2() throws ZookeeperException {
        this.zookeeper.registerPathChildrenWatcher(key,
            new ZookeeperPathChildrenWatcher<String, Serializable>() {
                @Override
                public void childAdd(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("childAdd=====>>>{Path:%s,Data:%s}%n", s, s2);
                    Assertions.assertThat(s).isNotNull().isNotBlank();
                }

                @Override
                public void childRemove(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("childRemove=====>>>{Path:%s,Data:%s}%n", s, s2);
                    Assertions.assertThat(s).isNull();
                }

                @Override
                public void childUpdate(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("childUpdate=====>>>{Path:%s,Data:%s}%n", s, s2);
                    Assertions.assertThat(s2).isNotNull();
                    //key updated
                    Assertions.assertThat(s2).isEqualTo("update");
                }

                @Override
                public void initialized(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("initialized=====>>>{Path:%s,Data:%s}%n", s, s2);
                }

                @Override
                public void connectLost(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("connectLost=====>>>{Path:%s,Data:%s}%n", s, s2);
                }

                @Override
                public void connectSuspended(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("connectSuspended=====>>>{Path:%s,Data:%s}%n", s, s2);
                }

                @Override
                public void connectReconnect(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("connectReconnect=====>>>{Path:%s,Data:%s}%n", s, s2);
                }
            }
        ).create(key + "/seconds").update(key + "/seconds", "update").delete(key + "/seconds");
    }

    /**
     * watcher3 test
     *
     * @throws ZookeeperException failed
     */
    @Test
    public void zookeeperWatcher3() throws ZookeeperException {
        String key2 = this.key + "/second";
        this.zookeeper.registerPathAndChildrenWatcher(key,
            new ZookeeperPathAndChildrenWatcher<String, Serializable>() {
                @Override
                public void pathAdd(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("pathAdd=====>>>{Path:%s,Data:%s}", s, s2);
                    Assertions.assertThat(s).isNotNull().isNotBlank();
                    //key2 created
                    Assertions.assertThat(s).isEqualTo(key2);
                    Assertions.assertThat(s2).isNotNull();
                    //key2 created
                    Assertions.assertThat(s2).isEqualTo("value2");
                }

                @Override
                public void pathRemove(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("pathRemove=====>>>{Path:{%s},Data:{%s}}", s, s2);
                    //key deleted
                    Assertions.assertThat(s).isNull();
                    Assertions.assertThat(s2).isNull();
                }

                @Override
                public void pathUpdate(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("pathUpdate=====>>>{Path:{%s},Data:{%s}}", s, s2);
                    //key updated
                    Assertions.assertThat(s).isNotNull();
                    Assertions.assertThat(s).isEqualTo(key);
                    Assertions.assertThat(s2).isNotNull();
                    Assertions.assertThat(s2).isEqualTo("update");
                }

                @Override
                public void initialized(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("initialized=====>>>{Path:%s,Data:%s}%n", s, s2);
                }

                @Override
                public void connectLost(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("connectLost=====>>>{Path:%s,Data:%s}%n", s, s2);
                }

                @Override
                public void connectSuspended(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("connectSuspended=====>>>{Path:%s,Data:%s}%n", s, s2);
                }

                @Override
                public void connectReconnect(KingsZookeeper operator, String s, Serializable s2) {
                    System.out.printf("connectReconnect=====>>>{Path:%s,Data:%s}%n", s, s2);
                }
            }
        ).update(key, "update").create(key2, "value2").delete(key2);
    }

    /**
     * zk事物操作 正常提交
     */
    @Test
    public void testTransactCommit() throws ZookeeperException {
        Collection<?> result = this.zookeeper.inTransaction(o -> {
            try {
                o.update(transKey, "zookeeperTransact update").delete(this.key);
            } catch (ZookeeperException ignore) {
            }
        });
        Assertions.assertThat(result).isNotEmpty();
        //check submit
        Assertions.assertThat(this.zookeeper.get(transKey)).isNotNull()
            .isEqualTo("zookeeperTransact update");
        Assertions.assertThat(this.zookeeper.contains(this.key)).isFalse();
    }

    @Test(expected = RuntimeException.class)
    public void testTransactRollback() throws ZookeeperException {
        //test for rollback ...
        Collection<?> rollback = this.zookeeper.inTransaction(o -> {
            try {
                o.update(this.transKey, "zookeeperTransact update2")
                    .update(this.key, "value update");
                throw new RuntimeException("rollback");
            } catch (ZookeeperException ignore) {
            }
        });
        Assertions.assertThat(rollback).isNotEmpty();
        //value为'zookeeperTransact update' 说明还是上一次的值 这次未提交
        Assertions.assertThat(this.zookeeper.get(key)).isNull();
        //rollbackKey不存在说明未提交
        Assertions.assertThat(this.zookeeper.get(transKey))
            .isNotEqualTo("zookeeperTransact update2");
    }

    @Test
    public void zookeeperAsync() throws Exception {
        this.zookeeper.inAsync(action -> {
                try {
                    action.create(asyncKey)
                        .delete(asyncKey)
                        .update(key, "zookeeperAsync update");
                } catch (ZookeeperException ignore) {
                }
            }, (k, v) -> {
                System.out.printf("\n===>>>Thread:%s,Key:%s,Value:%s%n",
                    Thread.currentThread().getName(), k, v);
                if (Objects.equals(v.getKey(), key)) {
                    try {
                        Assertions.assertThat(this.zookeeper.get(this.key)).isNotNull()
                            .isEqualTo("zookeeperAsync update");
                    } catch (ZookeeperException e) {
                        e.printStackTrace();
                    }
                }
            }
            , (a, v) -> System.out.printf("\n===>>>ERROR:%s,value:%s", a.getMessage(), v)
        );
    }
}
