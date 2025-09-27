package com.pharmaresolve.medcom.web.rest;

import com.pharmaresolve.medcom.service.ProductAvailabilityMonitoringService;
import com.pharmaresolve.medcom.service.scheduler.ProductAvailabilitySchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing product availability monitoring.
 */
@RestController
@RequestMapping("/api/monitoring")
public class ProductAvailabilityMonitoringResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAvailabilityMonitoringResource.class);

    private final ProductAvailabilityMonitoringService monitoringService;
    private final ProductAvailabilitySchedulerService schedulerService;

    public ProductAvailabilityMonitoringResource(
        ProductAvailabilityMonitoringService monitoringService,
        ProductAvailabilitySchedulerService schedulerService
    ) {
        this.monitoringService = monitoringService;
        this.schedulerService = schedulerService;
    }

    /**
     * GET /api/monitoring/status : Get monitoring system status.
     *
     * @return scheduler and monitoring status
     */
    @GetMapping("/status")
    public ResponseEntity<MonitoringStatusResponse> getMonitoringStatus() {
        LOG.debug("REST request to get monitoring status");

        ProductAvailabilitySchedulerService.SchedulerStatus schedulerStatus = schedulerService.getSchedulerStatus();
        ProductAvailabilityMonitoringService.MonitoringStats monitoringStats = monitoringService.getMonitoringStats();

        MonitoringStatusResponse response = new MonitoringStatusResponse();
        response.setSchedulerStatus(schedulerStatus);
        response.setMonitoringStats(monitoringStats);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/monitoring/trigger/{priority} : Manually trigger monitoring for specific priority.
     *
     * @param priority the priority level to trigger
     * @return number of items processed
     */
    @PostMapping("/trigger/{priority}")
    public ResponseEntity<TriggerResponse> triggerPriorityMonitoring(@PathVariable Integer priority) {
        LOG.debug("REST request to trigger monitoring for priority: {}", priority);

        if (priority < 1 || priority > 3) {
            return ResponseEntity.badRequest()
                .body(new TriggerResponse(0, "Invalid priority. Must be between 1 and 3."));
        }

        try {
            int processedItems = schedulerService.triggerPriorityMonitoring(priority);
            return ResponseEntity.ok(new TriggerResponse(processedItems,
                String.format("Successfully processed %d items for priority %d", processedItems, priority)));
        } catch (Exception e) {
            LOG.error("Error triggering monitoring for priority: {}", priority, e);
            return ResponseEntity.internalServerError()
                .body(new TriggerResponse(0, "Error triggering monitoring: " + e.getMessage()));
        }
    }

    /**
     * POST /api/monitoring/trigger-all : Manually trigger monitoring for all priorities.
     *
     * @return number of items processed
     */
    @PostMapping("/trigger-all")
    public ResponseEntity<TriggerResponse> triggerAllMonitoring() {
        LOG.debug("REST request to trigger monitoring for all priorities");

        try {
            int processedItems = monitoringService.processAllAvailabilityChecks();
            return ResponseEntity.ok(new TriggerResponse(processedItems,
                String.format("Successfully processed %d items across all priorities", processedItems)));
        } catch (Exception e) {
            LOG.error("Error triggering full monitoring", e);
            return ResponseEntity.internalServerError()
                .body(new TriggerResponse(0, "Error triggering monitoring: " + e.getMessage()));
        }
    }

    /**
     * POST /api/monitoring/scheduler/restart : Restart all schedulers.
     *
     * @return confirmation message
     */
    @PostMapping("/scheduler/restart")
    public ResponseEntity<String> restartSchedulers() {
        LOG.debug("REST request to restart all schedulers");

        try {
            schedulerService.restartAllSchedulers();
            return ResponseEntity.ok("All schedulers restarted successfully");
        } catch (Exception e) {
            LOG.error("Error restarting schedulers", e);
            return ResponseEntity.internalServerError()
                .body("Error restarting schedulers: " + e.getMessage());
        }
    }

    /**
     * POST /api/monitoring/scheduler/stop : Stop all schedulers.
     *
     * @return confirmation message
     */
    @PostMapping("/scheduler/stop")
    public ResponseEntity<String> stopSchedulers() {
        LOG.debug("REST request to stop all schedulers");

        try {
            schedulerService.stopAllSchedulers();
            return ResponseEntity.ok("All schedulers stopped successfully");
        } catch (Exception e) {
            LOG.error("Error stopping schedulers", e);
            return ResponseEntity.internalServerError()
                .body("Error stopping schedulers: " + e.getMessage());
        }
    }

    /**
     * POST /api/monitoring/scheduler/start/{priority} : Start scheduler for specific priority.
     *
     * @param priority the priority level
     * @return confirmation message
     */
    @PostMapping("/scheduler/start/{priority}")
    public ResponseEntity<String> startPriorityScheduler(@PathVariable Integer priority) {
        LOG.debug("REST request to start scheduler for priority: {}", priority);

        if (priority < 1 || priority > 3) {
            return ResponseEntity.badRequest().body("Invalid priority. Must be between 1 and 3.");
        }

        try {
            schedulerService.startPriorityScheduler(priority);
            return ResponseEntity.ok(String.format("Scheduler for priority %d started successfully", priority));
        } catch (Exception e) {
            LOG.error("Error starting scheduler for priority: {}", priority, e);
            return ResponseEntity.internalServerError()
                .body("Error starting scheduler: " + e.getMessage());
        }
    }

    /**
     * POST /api/monitoring/scheduler/stop/{priority} : Stop scheduler for specific priority.
     *
     * @param priority the priority level
     * @return confirmation message
     */
    @PostMapping("/scheduler/stop/{priority}")
    public ResponseEntity<String> stopPriorityScheduler(@PathVariable Integer priority) {
        LOG.debug("REST request to stop scheduler for priority: {}", priority);

        if (priority < 1 || priority > 3) {
            return ResponseEntity.badRequest().body("Invalid priority. Must be between 1 and 3.");
        }

        try {
            schedulerService.stopPriorityScheduler(priority);
            return ResponseEntity.ok(String.format("Scheduler for priority %d stopped successfully", priority));
        } catch (Exception e) {
            LOG.error("Error stopping scheduler for priority: {}", priority, e);
            return ResponseEntity.internalServerError()
                .body("Error stopping scheduler: " + e.getMessage());
        }
    }

    /**
     * Response DTO for monitoring status.
     */
    public static class MonitoringStatusResponse {
        private ProductAvailabilitySchedulerService.SchedulerStatus schedulerStatus;
        private ProductAvailabilityMonitoringService.MonitoringStats monitoringStats;

        public ProductAvailabilitySchedulerService.SchedulerStatus getSchedulerStatus() {
            return schedulerStatus;
        }

        public void setSchedulerStatus(ProductAvailabilitySchedulerService.SchedulerStatus schedulerStatus) {
            this.schedulerStatus = schedulerStatus;
        }

        public ProductAvailabilityMonitoringService.MonitoringStats getMonitoringStats() {
            return monitoringStats;
        }

        public void setMonitoringStats(ProductAvailabilityMonitoringService.MonitoringStats monitoringStats) {
            this.monitoringStats = monitoringStats;
        }

        @Override
        public String toString() {
            return "MonitoringStatusResponse{" +
                "schedulerStatus=" + schedulerStatus +
                ", monitoringStats=" + monitoringStats +
                '}';
        }
    }

    /**
     * Response DTO for trigger operations.
     */
    public static class TriggerResponse {
        private int processedItems;
        private String message;

        public TriggerResponse() {}

        public TriggerResponse(int processedItems, String message) {
            this.processedItems = processedItems;
            this.message = message;
        }

        public int getProcessedItems() {
            return processedItems;
        }

        public void setProcessedItems(int processedItems) {
            this.processedItems = processedItems;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "TriggerResponse{" +
                "processedItems=" + processedItems +
                ", message='" + message + '\'' +
                '}';
        }
    }
}