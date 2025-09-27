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

        String schedulerStatus = schedulerService.getSchedulerStatus();
        ProductAvailabilityMonitoringService.MonitoringStats monitoringStats = monitoringService.getMonitoringStats();

        MonitoringStatusResponse response = new MonitoringStatusResponse();
        response.setSchedulerStatusMessage(schedulerStatus);
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
            schedulerService.triggerManualCheck(priority);
            return ResponseEntity.ok(new TriggerResponse(0,
                String.format("Successfully triggered monitoring for priority %d", priority)));
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
     * Response DTO for monitoring status.
     */
    public static class MonitoringStatusResponse {
        private String schedulerStatusMessage;
        private ProductAvailabilityMonitoringService.MonitoringStats monitoringStats;

        public String getSchedulerStatusMessage() {
            return schedulerStatusMessage;
        }

        public void setSchedulerStatusMessage(String schedulerStatusMessage) {
            this.schedulerStatusMessage = schedulerStatusMessage;
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
                "schedulerStatusMessage='" + schedulerStatusMessage + '\'' +
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