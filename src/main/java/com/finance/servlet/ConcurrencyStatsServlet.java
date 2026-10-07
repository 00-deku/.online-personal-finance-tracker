package com.finance.servlet;

import com.finance.concurrent.ConcurrencyMonitor;
import com.finance.concurrent.ConcurrencyMonitor.BatchSummary;
import com.finance.concurrent.ConcurrencyMonitor.PoolSample;
import com.finance.concurrent.TaskExecutor;
import com.finance.concurrent.TaskRecord;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * JSON snapshot of the worker pool, polled by the live monitor on the Docs page.
 * Contains only aggregate numbers and task labels, never user data.
 */
@WebServlet("/api/concurrency")
public class ConcurrencyStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThreadPoolExecutor pool = TaskExecutor.get();

        StringBuilder json = new StringBuilder(2048);
        json.append('{');
        json.append("\"poolSize\":").append(pool.getPoolSize()).append(',');
        json.append("\"activeThreads\":").append(pool.getActiveCount()).append(',');
        json.append("\"queued\":").append(pool.getQueue().size()).append(',');
        json.append("\"largestPoolSize\":").append(pool.getLargestPoolSize()).append(',');
        json.append("\"submitted\":").append(ConcurrencyMonitor.getSubmitted()).append(',');
        json.append("\"completed\":").append(ConcurrencyMonitor.getCompleted()).append(',');
        json.append("\"failed\":").append(ConcurrencyMonitor.getFailed()).append(',');
        json.append("\"batches\":").append(ConcurrencyMonitor.getBatches()).append(',');
        json.append("\"totalSavedMs\":").append(ConcurrencyMonitor.getTotalSavedMs()).append(',');

        json.append("\"samples\":[");
        List<PoolSample> samples = ConcurrencyMonitor.getSamples();
        for (int i = 0; i < samples.size(); i++) {
            PoolSample s = samples.get(i);
            if (i > 0) json.append(',');
            json.append("{\"time\":").append(s.getTime())
                .append(",\"poolSize\":").append(s.getPoolSize())
                .append(",\"active\":").append(s.getActive())
                .append(",\"queued\":").append(s.getQueued())
                .append('}');
        }
        json.append("],");

        json.append("\"batchesRecent\":[");
        List<BatchSummary> batches = ConcurrencyMonitor.getRecentBatches();
        for (int i = 0; i < batches.size(); i++) {
            BatchSummary b = batches.get(i);
            if (i > 0) json.append(',');
            json.append("{\"name\":").append(quote(b.getName()))
                .append(",\"tasks\":").append(b.getTaskCount())
                .append(",\"threads\":").append(b.getThreadCount())
                .append(",\"wallMs\":").append(b.getWallMs())
                .append(",\"sequentialMs\":").append(b.getSequentialMs())
                .append(",\"finishedAt\":").append(b.getFinishedAt())
                .append('}');
        }
        json.append("],");

        json.append("\"tasksRecent\":[");
        List<TaskRecord> tasks = ConcurrencyMonitor.getRecentTasks();
        for (int i = 0; i < tasks.size(); i++) {
            TaskRecord t = tasks.get(i);
            if (i > 0) json.append(',');
            json.append("{\"batch\":").append(quote(t.getBatchName()))
                .append(",\"label\":").append(quote(t.getLabel()))
                .append(",\"thread\":").append(quote(t.getThreadName()))
                .append(",\"durationMs\":").append(t.getDurationMs())
                .append(",\"status\":").append(quote(t.getStatus()))
                .append('}');
        }
        json.append("]}");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(json.toString());
    }

    private static String quote(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(value.length() + 2).append('"');
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }
}
