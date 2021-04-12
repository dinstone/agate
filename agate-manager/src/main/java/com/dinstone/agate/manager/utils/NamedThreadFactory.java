package com.dinstone.agate.manager.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger index = new AtomicInteger();

    private String prex;

    public NamedThreadFactory(String prex) {
        this.prex = prex;
    }

    public Thread newThread(Runnable r) {
        return new Thread(r, prex + index.incrementAndGet());
    }

}