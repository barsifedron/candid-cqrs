package com.barsifedron.candid.cqrs.springboot.sample;

import java.util.concurrent.atomic.AtomicLong;

public class ThingsDoneCounter {

    private static final AtomicLong counter = new AtomicLong();

    public void increment() {
        counter.incrementAndGet();
    }

    public long value() {
        return counter.get();
    }
}
