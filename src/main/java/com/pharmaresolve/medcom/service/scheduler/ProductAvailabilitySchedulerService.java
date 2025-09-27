package com.pharmaresolve.medcom.service.scheduler;

import com.pharmaresolve.medcom.service.ProductAvailabilityMonitoringService;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

/**
 * Scheduler service for product availability monitoring with priority-based intervals.
 * Priority 1 = every 1 minute, Priority 2 = every 2 minutes, etc.
 */
@Service
public class ProductAvailabilitySchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAvailabilitySchedulerService.class);

    private final ProductAvailabilityMonitoringService monitoringService;
    private final TaskScheduler taskScheduler;

    // Track scheduled tasks for each priority
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    // Configuration
    private static final int MAX_PRIORITY = 3; // Monitor priorities 1-3
    private static final boolean ENABLED = true; // Can be configured via properties

    public ProductAvailabilitySchedulerService(
        ProductAvailabilityMonitoringService monitoringService,
        TaskScheduler taskScheduler
    ) {
        this.monitoringService = monitoringService;
        this.taskScheduler = taskScheduler;
    }

    /**
     * Initialize and start all priority-based schedulers when the application starts.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeSchedulers() {
        if (!ENABLED) {
            LOG.info("Product availability scheduling is disabled");
            return;
        }

        LOG.info("Initializing product availability schedulers for priorities 1-{}", MAX_PRIORITY);

        for (int priority = 1; priority <= MAX_PRIORITY; priority++) {
            startPriorityScheduler(priority);
        }

        LOG.info("Product availability schedulers initialized successfully");
    }

    /**
     * Start scheduler for a specific priority.
     *
     * @param priority the priority level (1 = every 1 minute, 2 = every 2 minutes, etc.)
     */
    public void startPriorityScheduler(int priority) {
        if (priority < 1 || priority > MAX_PRIORITY) {
            LOG.warn("Invalid priority: {}. Must be between 1 and {}", priority, MAX_PRIORITY);
            return;
        }

        // Stop existing scheduler if running
        stopPriorityScheduler(priority);

        // Create cron expression for the priority interval
        String cronExpression = createCronExpression(priority);

        String intervalDescription = getIntervalDescription(priority);
        LOG.info("Starting scheduler for priority {} with interval: {} (cron: {})",
            priority, intervalDescription, cronExpression);

        // Schedule the task
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
            () -> executePriorityMonitoring(priority),
            new CronTrigger(cronExpression)
        );

        scheduledTasks.put(priority, scheduledTask);
        LOG.debug("Scheduler started for priority: {}", priority);
    }

    /**
     * Stop scheduler for a specific priority.
     *
     * @param priority the priority level
     */
    public void stopPriorityScheduler(int priority) {
        ScheduledFuture<?> task = scheduledTasks.remove(priority);
        if (task != null && !task.isCancelled()) {
            task.cancel(false);
            LOG.info("Stopped scheduler for priority: {}", priority);
        }
    }

    /**
     * Stop all schedulers.
     */
    public void stopAllSchedulers() {
        LOG.info("Stopping all product availability schedulers");

        scheduledTasks.forEach((priority, task) -> {
            if (task != null && !task.isCancelled()) {
                task.cancel(false);
                LOG.debug("Stopped scheduler for priority: {}", priority);
            }
        });

        scheduledTasks.clear();
        LOG.info("All schedulers stopped");
    }

    /**
     * Restart all schedulers.
     */
    public void restartAllSchedulers() {
        LOG.info("Restarting all product availability schedulers");
        stopAllSchedulers();
        initializeSchedulers();
    }

    /**
     * Get scheduler status.
     *
     * @return scheduler status information
     */
    public SchedulerStatus getSchedulerStatus() {
        SchedulerStatus status = new SchedulerStatus();
        status.setEnabled(ENABLED);
        status.setMaxPriority(MAX_PRIORITY);

        scheduledTasks.forEach((priority, task) -> {
            boolean isRunning = task != null && !task.isCancelled() && !task.isDone();
            status.addPriorityStatus(priority, isRunning);
        });

        return status;
    }

    /**
     * Execute monitoring for a specific priority asynchronously.
     *
     * @param priority the priority level
     */
    @Async
    public void executePriorityMonitoring(int priority) {
        try {
            LOG.debug("Executing availability monitoring for priority: {}", priority);
            ZonedDateTime startTime = ZonedDateTime.now();

            int processedItems = monitoringService.processAvailabilityForPriority(priority);

            ZonedDateTime endTime = ZonedDateTime.now();
            long durationMs = java.time.Duration.between(startTime, endTime).toMillis();

            LOG.info("Completed availability monitoring for priority: {}. Processed: {} items in {}ms",
                priority, processedItems, durationMs);

        } catch (Exception e) {
            LOG.error("Error executing availability monitoring for priority: {}", priority, e);
        }
    }

    /**
     * Create cron expression for priority-based interval.
     * Priority 1 = every minute, Priority 2 = every 30 minutes, Priority 3 = every hour
     *
     * @param priority the priority level
     * @return cron expression
     */
    private String createCronExpression(int priority) {
        return switch (priority) {
            case 1 -> "0 */1 * * * *";  // Every minute
            case 2 -> "0 */30 * * * *"; // Every 30 minutes
            case 3 -> "0 0 */1 * * *";  // Every hour
            default ->
                throw new IllegalArgumentException("Invalid priority: " + priority + ". Must be between 1 and 3.");
        };
    }

    /**
     * Get interval description for priority.
     *
     * @param priority the priority level
     * @return human-readable interval description
     */
    private String getIntervalDescription(int priority) {
        return switch (priority) {
            case 1 -> "every minute";
            case 2 -> "every 30 minutes";
            case 3 -> "every hour";
            default -> "unknown";
        };
    }

    /**
     * Manual trigger for specific priority (for testing/admin purposes).
     *
     * @param priority the priority to trigger
     * @return number of items processed
     */
    public int triggerPriorityMonitoring(int priority) {
        LOG.info("Manual trigger for priority: {}", priority);
        return monitoringService.processAvailabilityForPriority(priority);
    }

    /**
     * Scheduler status information.
     */
    public static class SchedulerStatus {
        private boolean enabled;
        private int maxPriority;
        private java.util.Map<Integer, Boolean> priorityStatuses = new java.util.HashMap<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxPriority() {
            return maxPriority;
        }

        public void setMaxPriority(int maxPriority) {
            this.maxPriority = maxPriority;
        }

        public java.util.Map<Integer, Boolean> getPriorityStatuses() {
            return priorityStatuses;
        }

        public void setPriorityStatuses(java.util.Map<Integer, Boolean> priorityStatuses) {
            this.priorityStatuses = priorityStatuses;
        }

        public void addPriorityStatus(int priority, boolean isRunning) {
            this.priorityStatuses.put(priority, isRunning);
        }

        public int getActiveSchedulers() {
            return (int) priorityStatuses.values().stream().mapToLong(status -> status ? 1 : 0).sum();
        }

        @Override
        public String toString() {
            return "SchedulerStatus{" +
                "enabled=" + enabled +
                ", maxPriority=" + maxPriority +
                ", activeSchedulers=" + getActiveSchedulers() +
                ", priorityStatuses=" + priorityStatuses +
                '}';
        }
    }
}
