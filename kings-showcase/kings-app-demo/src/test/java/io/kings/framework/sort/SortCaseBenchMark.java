package io.kings.framework.sort;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgsPrepend = "-XX:+PrintStringTableStatistics")
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class SortCaseBenchMark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(SortCaseBenchMark.class.getSimpleName())
            .build();
        new Runner(opt).run();
    }

    @Param({"1", "10"})
    private int count;

    @Benchmark
    public void oooBenchmark() {
        sort(this::ooo);
    }

    @Benchmark
    public void quickSortBenchmark() {
        sort(a -> quickSort(a, 0, a.length - 1));
    }

    private void sort(Consumer<int[]> consumer) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) {
            int random = (int) (Math.random() * count);
            a[i] = random;
        }
        consumer.accept(a);
    }

    /**
     * 冒泡排序ooo
     */
    private void ooo(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int k = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = k;
                }
            }
        }
    }
    //快速排序算法

    /**
     * 快速排序quickSort
     */
    private void quickSort(int[] a, int low, int high) {
        if (low < high) {
            int pivot = partition(a, low, high);
            quickSort(a, low, pivot - 1);
            quickSort(a, pivot + 1, high);
        }
    }

    private int partition(int[] a, int low, int high) {
        int key = a[low];
        int temp;
        while (low < high) {
            for (; low < high; high--) {
                if (a[high] < key) {
                    temp = a[high];
                    a[high] = a[low];
                    a[low] = temp;
                    break;
                }
            }
            for (; low < high; low++) {
                if (a[low] > key) {
                    temp = a[high];
                    a[high] = a[low];
                    a[low] = temp;
                    break;
                }
            }
        }
        return low;
    }
}
