package com.kings.base;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class MyThread extends Thread {
    MyThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.printf("Thread:id[%s] name:[%s] running \n", getId(), getName());
        LockSupport.park();
        System.out.println("T1运行结束");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getState());
        Integer i = new Integer(1);
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (i) {
                i.notify();
            }
        }).start();
        synchronized (i) {
            i.wait();
        }
        System.out.println(Thread.currentThread().getState());

    }

    static class Parker {
        @Override
        public String toString() {
            return "Packer";
        }
    }
}
