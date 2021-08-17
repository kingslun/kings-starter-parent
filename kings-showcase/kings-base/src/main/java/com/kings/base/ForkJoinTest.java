package com.kings.base;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTest {

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        // 第8项为21
        FibonacciTask task = new FibonacciTask(100);
        FibonacciTask2 task2 = new FibonacciTask2(100);

        int result1 = forkJoinPool.invoke(task);
        int result2 = forkJoinPool.invoke(task2);
        System.out.println(result1 + ":" + result2);

        // 计算1到n的和
        SumNums task3 = new SumNums(100);
        int result3 = forkJoinPool.invoke(task3);
        System.out.println(result3);

        forkJoinPool.shutdown();
    }

    // 斐波那契数列：0、1、1、2、3、5、8、13、21、……
    // 在数学上，斐波纳契数列以如下被以递归的方法定义：F0=0，F1=1，Fn=F(n-1)+F(n-2)（n>=2，n∈N*）
    // 获取斐波那契数量的第n项的值，如第0项为0，第1项为1
    private static class FibonacciTask extends RecursiveTask<Integer> {
        // n为斐波那契数列的第几项
        final int n;

        FibonacciTask(int n) {
            this.n = n;
        }

        protected Integer compute() {
            if (n <= 1) {
                return n;
            }
            FibonacciTask f1 = new FibonacciTask(n - 1);
            // fork分解为子任务
            f1.fork();

            FibonacciTask f2 = new FibonacciTask(n - 2);
            // f2不再分解
            return f2.compute() + f1.join();
        }
    }

    private static class FibonacciTask2 extends RecursiveTask<Integer> {
        // n为斐波那契数列的第几项
        final int n;

        FibonacciTask2(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) {
                return n;
            }
            FibonacciTask2 f1 = new FibonacciTask2(n - 1);
            // fork分解为子任务
            f1.fork();
            FibonacciTask2 f2 = new FibonacciTask2(n - 2);
            // f2继续分解
            f2.fork();
            return f2.join() + f1.join();
        }
    }

    // 累加1到n的顺序数组：f(n) = 1+2+..+n
    private static class SumNums extends RecursiveTask<Integer> {
        private final int n;
        private int result;

        public SumNums(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n == 0) {
                return n;
            }
            SumNums s = new SumNums(n - 1);
            s.fork();
            return s.join() + n;
        }
    }
}