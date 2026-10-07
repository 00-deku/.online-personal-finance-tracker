package com.finance.concurrent;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Starts the shared worker pool and a background scheduler when Tomcat deploys the app,
 * and shuts both down cleanly on undeploy so no threads leak across redeployments.
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    private static final long SAMPLE_INTERVAL_SECONDS = 5;

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        TaskExecutor.get();

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "finance-scheduler");
            t.setDaemon(true);
            return t;
        });

        // Periodic background job: sample pool size, active threads and queue depth.
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ConcurrencyMonitor.samplePool();
            } catch (RuntimeException e) {
                // An uncaught exception would silently cancel all future runs.
                event.getServletContext().log("Pool sampling failed", e);
            }
        }, 0, SAMPLE_INTERVAL_SECONDS, TimeUnit.SECONDS);

        event.getServletContext().log("Finance worker pool started: core=" + TaskExecutor.getCoreThreads()
                + ", max=" + TaskExecutor.getMaxThreads());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        TaskExecutor.shutdown();
    }

    public static long getSampleIntervalSeconds() {
        return SAMPLE_INTERVAL_SECONDS;
    }
}
