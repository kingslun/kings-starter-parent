package io.kings.framework.log.desensitize;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * intern性能测试 <a href="https://zhuanlan.zhihu.com/p/164671565">参考文献</a>
 * <br>Benchmark                                (size)  Mode  Cnt       Score        Error  Units
 * <br>StringInternBenchMark.useCurrentHashMap       1  avg    3      36.515 ±      4.229  ns/op
 * <br>StringInternBenchMark.useCurrentHashMap     100  avg    3    3707.500 ±   1087.636  ns/op
 * <br>StringInternBenchMark.useCurrentHashMap    1000  avg    3   43495.997 ±  18188.882  ns/op
 * <br>StringInternBenchMark.useHashMap              1  avg    3      24.972 ±      7.935  ns/op
 * <br>StringInternBenchMark.useHashMap            100  avg    3    2758.187 ±    918.606  ns/op
 * <br>StringInternBenchMark.useHashMap           1000  avg    3   31909.206 ±  18297.360  ns/op
 * <br>StringInternBenchMark.useIntern               1  avg    3     135.058 ±    223.509  ns/op
 * <br>StringInternBenchMark.useIntern             100  avg    3   12581.444 ±   2429.061  ns/op
 * <br>StringInternBenchMark.useIntern            1000  avg    3  155542.550 ± 244663.257  ns/op
 * 综上native方法不一定快。intern的用处不是在于速度，而是在于节约Heap中的内存使用
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 1, jvmArgsPrepend = "-XX:+PrintStringTableStatistics")
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class StringInternBenchMark {

    @Param({"1", "100", "1000"})
    private int size;

    private StringIntern str;
    private ConcurrentHashMapIntern chm;
    private HashMapIntern hm;

    @Setup
    public void setup() {
        str = new StringIntern();
        chm = new ConcurrentHashMapIntern();
        hm = new HashMapIntern();
    }

    static class StringIntern {

        public String intern(String s) {
            return s.intern();
        }
    }

    @Benchmark
    public void useIntern(Blackhole bh) {
        for (int c = 0; c < size; c++) {
            bh.consume(str.intern("doit" + c));
        }
    }

    static class ConcurrentHashMapIntern {

        private final Map<String, String> map;

        public ConcurrentHashMapIntern() {
            map = new ConcurrentHashMap<>();
        }

        public String intern(String s) {
            String exist = map.putIfAbsent(s, s);
            return (exist == null) ? s : exist;
        }
    }

    @Benchmark
    public void useCurrentHashMap(Blackhole bh) {
        for (int c = 0; c < size; c++) {
            bh.consume(chm.intern("doit" + c));
        }
    }

    static class HashMapIntern {

        private final Map<String, String> map;

        HashMapIntern() {
            map = new HashMap<>();
        }

        public String intern(String s) {
            String exist = map.putIfAbsent(s, s);
            return (exist == null) ? s : exist;
        }
    }

    @Benchmark
    public void useHashMap(Blackhole bh) {
        for (int c = 0; c < size; c++) {
            bh.consume(hm.intern("doit" + c));
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(StringInternBenchMark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
