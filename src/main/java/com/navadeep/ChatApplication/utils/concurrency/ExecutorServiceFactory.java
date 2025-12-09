package com.navadeep.ChatApplication.utils.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceFactory {

    private static final int CORES = Runtime.getRuntime().availableProcessors();

    /**
     * Creates a virtual thread per task executor.
     */
    public ExecutorService createVirtualThreadExecutor() {
        // Custom thread factory to name threads
        ThreadFactory namedThreadFactory = Thread.ofVirtual()
                .name("chat-app-thread-", 1) // "chat-app-thread-1", "chat-app-thread-2", ...
                .factory();

        return Executors.newThreadPerTaskExecutor(namedThreadFactory);
    }


    /**
     * Thread factory with naming support.
     */
    static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger();
        private final String prefix;

        NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, prefix + counter.getAndIncrement());
            t.setDaemon(false);
            return t;
        }
    }

    /**
     * Main worker pool – business logic, DB calls, message processing
     */
    public ExecutorService createWorkerThreadPool() {
        return new ThreadPoolExecutor(
                CORES,                        // 12 core threads
                CORES * 2,                    // 24 max threads
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                new NamedThreadFactory("worker-"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * Broadcast pool – WebSocket serialization + sending
     */
    public ExecutorService createBroadcastThreadPool() {
        return new ThreadPoolExecutor(
                CORES / 2,                    // 6 threads
                CORES,                        // 12 threads max
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500),
                new NamedThreadFactory("broadcast-"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
