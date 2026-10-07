package com.finance.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Application-wide worker pool shared by every request.
 *
 * Creating a new pool per request (the old ReportService approach) spawns and tears down
 * threads on every page load. A single bounded pool reuses threads, caps the number of
 * parallel database connections, and applies back-pressure through CallerRunsPolicy:
 * when the queue is full the request thread executes the task itself instead of failing.
 */
public final class TaskExecutor {

    private static final int CORE_THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int MAX_THREADS = CORE_THREADS * 2;
    private static final int QUEUE_CAPACITY = 200;

    private static volatile ThreadPoolExecutor pool;

    private TaskExecutor() {
    }

    /**
     * Returns the shared pool, lazily creating it on first use (double-checked locking).
     */
    public static ThreadPoolExecutor get() {
        ThreadPoolExecutor local = pool;
        if (local == null || local.isShutdown()) {
            synchronized (TaskExecutor.class) {
                local = pool;
                if (local == null || local.isShutdown()) {
                    local = createPool();
                    pool = local;
                }
            }
        }
        return local;
    }

    private static ThreadPoolExecutor createPool() {
        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "finance-worker-" + counter.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_THREADS,
                MAX_THREADS,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                factory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * Graceful shutdown called when the web application is undeployed.
     */
    public static void shutdown() {
        ThreadPoolExecutor local = pool;
        if (local == null) {
            return;
        }
        local.shutdown();
        try {
            if (!local.awaitTermination(5, TimeUnit.SECONDS)) {
                local.shutdownNow();
            }
        } catch (InterruptedException e) {
            local.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static int getCoreThreads() { return CORE_THREADS; }
    public static int getMaxThreads() { return MAX_THREADS; }
    public static int getQueueCapacity() { return QUEUE_CAPACITY; }
}
