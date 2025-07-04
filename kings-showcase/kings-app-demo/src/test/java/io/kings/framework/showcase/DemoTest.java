package io.kings.framework.showcase;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class DemoTest {

    //建议返回空数组取代null
    private List<String> list(Predicate<Integer> predicate) {
        if (predicate.test(5)) {
            return Collections.emptyList();
        } else {
            return Arrays.asList("one", "two");
        }
    }

    @Test
    public void testList() {
        //如果list返回null 那么调用方可能会发生空指针
        List<String> list = list(i -> i > 0);
        Assertions.assertThat(list).isEmpty();
        //尽量使用Objects.equals比较两个对象 因为他避免了npe
        Assertions.assertThat(Objects.equals(null, list)).isTrue();
        //同理Objects的其他API也建议使用 比如hashcode等
        //非必要不自己创建线程 线程池建议采用如下方式创建
        new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                (r) -> new Thread(r, "ThreadName"),
                new ThreadPoolExecutor.AbortPolicy());
        //不建议使用以下Executors创建
        Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(1);
        Executors.newSingleThreadExecutor();
        //equals与hashcode一并重写 建议自定义拥有多个私有属性的对象都重写toString
        //todo
    }
}
