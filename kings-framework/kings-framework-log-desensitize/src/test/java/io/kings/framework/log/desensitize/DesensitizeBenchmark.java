package io.kings.framework.log.desensitize;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 脱敏性能测试
 * <pre>
 * Score:平均耗时值 Error:误差
 * Benchmark                          (printLogCount)  Mode  Cnt    Score     Error  Units
 * DesensitizeBenchmark.logBenchmark             1000  avg    3    6.724 ±   1.925  ms/op
 * DesensitizeBenchmark.logBenchmark            10000  avg    3   74.279 ±  21.641  ms/op
 * DesensitizeBenchmark.logBenchmark           100000  avg    3  708.833 ± 460.464  ms/op
 * </pre>
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgsPrepend = "-XX:+PrintStringTableStatistics")
//预热
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@Slf4j(topic = "desensitize-logger")
public class DesensitizeBenchmark {

    @Param({"1000", "10000", "100000"})
    private int printLogCount;

    @Benchmark
    public void logBenchmark() {
        for (int c = 0; c < printLogCount; c++) {
            log.info("mobile_phone=15021261772,chinese_name=张三丰");
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(DesensitizeBenchmark.class.getSimpleName())
            .build();
        new Runner(opt).run();
    }
}
