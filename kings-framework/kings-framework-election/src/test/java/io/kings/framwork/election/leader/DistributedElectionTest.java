package io.kings.framwork.election.leader;

import io.kings.framework.election.leader.AbstractDistributedElection;
import io.kings.framework.election.leader.DistributedElection;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@Configuration
@SpringBootTest(classes = DistributedElectionTest.class)
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
public class DistributedElectionTest {
    @Configuration
    @Slf4j
    static class Master1 extends AbstractDistributedElection implements DistributedElection {
        @Override
        public String name() {
            return "Master1";
        }

        @Override
        public void leader() {
            log.info("{} become leader", this.name());
        }
    }

    @Configuration
    @Slf4j
    static class Master2 extends AbstractDistributedElection implements DistributedElection {
        @Override
        public String name() {
            return "Master2";
        }

        @Override
        public void leader() {
            log.info("{} become leader", this.name());
        }
    }

    @Test(expected = IllegalAccessError.class)
    public void runToAboard() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
        throw new IllegalAccessError();
    }
}
