package com.finance.servlet;

import com.finance.concurrent.AppLifecycleListener;
import com.finance.concurrent.ConcurrencyMonitor;
import com.finance.concurrent.TaskExecutor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Public documentation page explaining the architecture, request flow and multithreading model.
 */
@WebServlet("/docs")
public class DocsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ThreadPoolExecutor pool = TaskExecutor.get();
        request.setAttribute("coreThreads", TaskExecutor.getCoreThreads());
        request.setAttribute("maxThreads", TaskExecutor.getMaxThreads());
        request.setAttribute("queueCapacity", TaskExecutor.getQueueCapacity());
        request.setAttribute("sampleInterval", AppLifecycleListener.getSampleIntervalSeconds());
        request.setAttribute("poolSize", pool.getPoolSize());
        request.setAttribute("activeThreads", pool.getActiveCount());
        request.setAttribute("tasksCompleted", ConcurrencyMonitor.getCompleted());
        request.setAttribute("batchesRun", ConcurrencyMonitor.getBatches());
        request.setAttribute("recentBatches", ConcurrencyMonitor.getRecentBatches());
        request.setAttribute("recentTasks", ConcurrencyMonitor.getRecentTasks());

        request.getRequestDispatcher("/WEB-INF/views/docs.jsp").forward(request, response);
    }
}
