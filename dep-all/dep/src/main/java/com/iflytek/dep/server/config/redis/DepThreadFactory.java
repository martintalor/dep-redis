package com.iflytek.dep.server.config.redis;

import java.util.concurrent.ThreadFactory;

public class DepThreadFactory implements ThreadFactory {
    private int counter;
    private String name;

    public DepThreadFactory(String name) {
        counter = 0;
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable run) {
        Thread t = new Thread(run, name + "-Thread-" + counter);
        counter++;
        return t;
    }
}