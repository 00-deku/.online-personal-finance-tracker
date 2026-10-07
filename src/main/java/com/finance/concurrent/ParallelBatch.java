package com.finance.concurrent;

import com.finance.exception.DatabaseException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A group of independent tasks forked onto the shared pool for a single request.
 *
 * Usage:
 * <pre>
 *   ParallelBatch batch = new ParallelBatch("User dashboard");
 *   CompletableFuture&lt;BigDecimal&gt; total = batch.fork("Total expenses", () -&gt; dao.total(id));
 *   CompletableFuture&lt;List&lt;Expense&gt;&gt; recent = batch.fork("Recent expenses", () -&gt; dao.recent(id));
 *   batch.awaitAll();                 // request thread waits once for every task
 *   BigDecimal t = ParallelBatch.result(total);
 * </pre>
 *
 * Each task records its thread name and timing so the UI can show how the work was split.
 */
public class ParallelBatch {

    /** A unit of work that may throw a checked exception (DAO calls throw DatabaseException). */
    @FunctionalInterface
    public interface Task<T> {
        T call() throws Exception;
    }

    private final String name;
    private final long startNanos;
    // Written from worker threads too (tasks chained with thenCompose fork more tasks), so it must be thread-safe.
    private final List<CompletableFuture<?>> futures = new CopyOnWriteArrayList<>();
    private final List<TaskRecord> records = new CopyOnWriteArrayList<>();
    private volatile long wallTimeMs = -1;

    public ParallelBatch(String name) {
        this.name = name;
        this.startNanos = System.nanoTime();
    }

    /**
     * Submits a task to the shared pool and returns its future immediately.
     */
    public <T> CompletableFuture<T> fork(String label, Task<T> task) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            long taskStart = System.nanoTime();
            boolean ok = false;
            try {
                T value = task.call();
                ok = true;
                return value;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new CompletionException(e);
            } finally {
                long end = System.nanoTime();
                TaskRecord record = new TaskRecord(
                        name,
                        label,
                        Thread.currentThread().getName(),
                        (taskStart - startNanos) / 1_000_000,
                        (end - taskStart) / 1_000_000,
                        ok);
                records.add(record);
                ConcurrencyMonitor.recordTask(record);
            }
        }, TaskExecutor.get());

        ConcurrencyMonitor.taskSubmitted();
        futures.add(future);
        return future;
    }

    /**
     * Blocks the calling (request) thread until every forked task has finished,
     * then publishes a summary of the batch to the monitor.
     */
    public void awaitAll() throws DatabaseException {
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            throw unwrap(e);
        } finally {
            wallTimeMs = (System.nanoTime() - startNanos) / 1_000_000;
            ConcurrencyMonitor.recordBatch(this);
        }
    }

    /**
     * Reads the value of a completed future, translating task failures into DatabaseException.
     */
    public static <T> T result(CompletableFuture<T> future) throws DatabaseException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw unwrap(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Request interrupted while waiting for parallel task.", e);
        }
    }

    private static DatabaseException unwrap(Throwable e) {
        Throwable cause = e;
        while ((cause instanceof CompletionException || cause instanceof ExecutionException)
                && cause.getCause() != null) {
            cause = cause.getCause();
        }
        if (cause instanceof DatabaseException de) {
            return de;
        }
        return new DatabaseException("Parallel task failed: " + cause.getMessage(), cause);
    }

    public String getName() { return name; }

    public long getWallTimeMs() { return wallTimeMs; }

    public int getTaskCount() { return records.size(); }

    /** Sum of every task's duration, i.e. roughly how long the same work takes sequentially. */
    public long getSequentialTimeMs() {
        long sum = 0;
        for (TaskRecord r : records) {
            sum += r.getDurationMs();
        }
        return sum;
    }

    public int getThreadCount() {
        Set<String> threads = new HashSet<>();
        for (TaskRecord r : records) {
            threads.add(r.getThreadName());
        }
        return threads.size();
    }

    /** Records sorted by start time, used for the timeline view. */
    public List<TaskRecord> getRecords() {
        List<TaskRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparingLong(TaskRecord::getStartOffsetMs));
        return sorted;
    }

    /** Longest task end time, used to scale timeline bars to 100%. */
    public long getTimelineSpanMs() {
        long span = 1;
        for (TaskRecord r : records) {
            span = Math.max(span, r.getStartOffsetMs() + r.getDurationMs());
        }
        return span;
    }
}
