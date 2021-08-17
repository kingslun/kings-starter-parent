package com.kings.base;

public class Shutdown {
    private static final class ShutDown0 extends Thread {
        private ShutDown0(String name) {
            super(name);
        }

        @Override
        public void run() {
        }
    }

    public static void main(String[] args) {
        ShutDown0 shutdown = new ShutDown0("close thread");
        Runtime.getRuntime().addShutdownHook(shutdown);
    }
}
