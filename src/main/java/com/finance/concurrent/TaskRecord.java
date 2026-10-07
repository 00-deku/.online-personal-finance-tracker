package com.finance.concurrent;

import java.io.Serializable;

/**
 * Immutable record of one task that ran on the worker pool: which thread ran it,
 * when it started relative to its batch, and how long it took.
 */
public class TaskRecord implements Serializable {

    private final String batchName;
    private final String label;
    private final String threadName;
    private final long startOffsetMs;
    private final long durationMs;
    private final boolean success;

    public TaskRecord(String batchName, String label, String threadName,
                      long startOffsetMs, long durationMs, boolean success) {
        this.batchName = batchName;
        this.label = label;
        this.threadName = threadName;
        this.startOffsetMs = startOffsetMs;
        this.durationMs = durationMs;
        this.success = success;
    }

    public String getBatchName() { return batchName; }
    public String getLabel() { return label; }
    public String getThreadName() { return threadName; }
    public long getStartOffsetMs() { return startOffsetMs; }
    public long getDurationMs() { return durationMs; }
    public boolean isSuccess() { return success; }
    public String getStatus() { return success ? "OK" : "FAILED"; }
}
