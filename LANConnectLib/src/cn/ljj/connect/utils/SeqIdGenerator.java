package cn.ljj.connect.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqIdGenerator {
    private static final AtomicInteger mSeqId = new AtomicInteger((int) System.currentTimeMillis());

    private SeqIdGenerator() {
    }

    public static int nextSeqId() {
        return mSeqId.incrementAndGet();
    }
}
