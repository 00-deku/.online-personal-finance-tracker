package com.finance.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe, in-memory statistics about the worker pool.
 *
 * Many request threads and worker threads write here at the same time, so every field uses a
 * lock-free concurrent type: AtomicLong counters and ConcurrentLinkedDeque ring buffers.
 * Only aggregate data and task labels are kept (never user data), so the snapshot is safe to
 * show on the public Docs page.
 */
public final class ConcurrencyMonitor {

    private static final int MAX_TASKS = 40;
    private static final int MAX_BATCHES = 10;
    private static final int MAX_SAMPLES = 30;

    private static final AtomicLong submitted = new AtomicLong();
    private static final AtomicLong completed = new AtomicLong();
    private static final AtomicLong failed = new AtomicLong();
    private static final AtomicLong batches = new AtomicLong();
    private static final AtomicLong totalSavedMs = new AtomicLong();

    private static final ConcurrentLinkedDeque<TaskRecord> recentTasks = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<BatchSummary> recentBatches = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<PoolSample> samples = new ConcurrentLinkedDeque<>();

    private ConcurrencyMonitor() {
    }

    static void taskSubmitted() {
        submitted.incrementAndGet();
    }

    static void recordTask(TaskRecord record) {
        if (record.isSuccess()) {
            completed.incrementAndGet();
        } else {
            failed.incrementAndGet();
        }
        pushBounded(recentTasks, record, MAX_TASKS);
    }

    static void recordBatch(ParallelBatch batch) {
        batches.incrementAndGet();
        BatchSummary summary = new BatchSummary(batch.getName(), batch.getTaskCount(), batch.getThreadCount(),
                batch.getWallTimeMs(), batch.getSequentialTimeMs(), System.currentTimeMillis());
        totalSavedMs.addAndGet(Math.max(0, summary.getSavedMs()));
        pushBounded(recentBatches, summary, MAX_BATCHES);
    }

    /** Called by the background scheduler thread every few seconds. */
    public static void samplePool() {
        ThreadPoolExecutor pool = TaskExecutor.get();
        PoolSample sample = new PoolSample(System.currentTimeMillis(), pool.getPoolSize(),
                pool.getActiveCount(), pool.getQueue().size(), pool.getCompletedTaskCount());
        pushBounded(samples, sample, MAX_SAMPLES);
    }

    private static <T> void pushBounded(ConcurrentLinkedDeque<T> deque, T item, int max) {
        deque.addFirst(item);
        while (deque.size() > max) {
            deque.pollLast();
        }
    }

    private static <T> List<T> copy(ConcurrentLinkedDeque<T> deque) {
        List<T> list = new ArrayList<>();
        Iterator<T> it = deque.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    public static long getSubmitted() { return submitted.get(); }
    public static long getCompleted() { return completed.get(); }
    public static long getFailed() { return failed.get(); }
    public static long getBatches() { return batches.get(); }
    public static long getTotalSavedMs() { return totalSavedMs.get(); }
    public static List<TaskRecord> getRecentTasks() { return copy(recentTasks); }
    public static List<BatchSummary> getRecentBatches() { return copy(recentBatches); }
    public static List<PoolSample> getSamples() { return copy(samples); }

    /** Snapshot of a finished batch (the futures themselves are not retained). */
    public static class BatchSummary {
        private final String name;
        private final int taskCount;
        private final int threadCount;
        private final long wallMs;
        private final long sequentialMs;
        private final long finishedAt;

        BatchSummary(String name, int taskCount, int threadCount, long wallMs, long sequentialMs, long finishedAt) {
            this.name = name;
            this.taskCount = taskCount;
            this.threadCount = threadCount;
            this.wallMs = wallMs;
            this.sequentialMs = sequentialMs;
            this.finishedAt = finishedAt;
        }

        public String getName() { return name; }
        public int getTaskCount() { return taskCount; }
        public int getThreadCount() { return threadCount; }
        public long getWallMs() { return wallMs; }
        public long getSequentialMs() { return sequentialMs; }
        public long getSavedMs() { return sequentialMs - wallMs; }
        public long getFinishedAt() { return finishedAt; }
    }

    /** One periodic reading of the pool's state. */
    public static class PoolSample {
        private final long time;
        private final int poolSize;
        private final int active;
        private final int queued;
        private final long completedTasks;

        PoolSample(long time, int poolSize, int active, int queued, long completedTasks) {
            this.time = time;
            this.poolSize = poolSize;
            this.active = active;
            this.queued = queued;
            this.completedTasks = completedTasks;
        }

        public long getTime() { return time; }
        public int getPoolSize() { return poolSize; }
        public int getActive() { return active; }
        public int getQueued() { return queued; }
        public long getCompletedTasks() { return completedTasks; }
    }
}
