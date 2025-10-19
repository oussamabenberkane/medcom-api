package com.pharmaresolve.medcom.service;

import com.pharmaresolve.medcom.domain.Alert;
import com.pharmaresolve.medcom.domain.WatchlistItem;
import com.pharmaresolve.medcom.repository.WatchlistItemRepository;
import com.pharmaresolve.medcom.service.dto.AlertDTO;
import com.pharmaresolve.medcom.service.external.SupplierApiService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for monitoring product availability and creating alerts on changes.
 */
@Service
@Transactional
public class ProductAvailabilityMonitoringService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAvailabilityMonitoringService.class);

    private final WatchlistItemRepository watchlistItemRepository;
    private final SupplierApiService supplierApiService;
    private final AlertService alertService;
    private final NotificationService notificationService;

    public ProductAvailabilityMonitoringService(
        WatchlistItemRepository watchlistItemRepository,
        SupplierApiService supplierApiService,
        AlertService alertService,
        NotificationService notificationService
    ) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.supplierApiService = supplierApiService;
        this.alertService = alertService;
        this.notificationService = notificationService;
    }

    /**
     * Process availability monitoring for watchlist items with specific priority.
     *
     * @param priority the priority level to process
     * @return number of items processed
     */
    public int processAvailabilityForPriority(int priority) {
        LOG.info("Starting availability monitoring for priority: {}", priority);

        List<WatchlistItem> items = findWatchlistItemsForMonitoring(priority);
        LOG.debug("Found {} watchlist items with priority {} for monitoring", items.size(), priority);

        int processedCount = 0;
        List<Alert> createdAlerts = new ArrayList<>();

        for (WatchlistItem item : items) {
            try {
                Alert alert = processWatchlistItemWithoutNotification(item);
                if (alert != null) {
                    createdAlerts.add(alert);
                }
                processedCount++;
            } catch (Exception e) {
                LOG.error("Error processing watchlist item: {} for product: {}",
                    item.getId(), item.getProduct().getName(), e);
            }
        }

        // Send consolidated notifications for all created alerts
        if (!createdAlerts.isEmpty()) {
            notificationService.createConsolidatedEmailNotifications(createdAlerts);
        }

        LOG.info("Completed availability monitoring for priority: {}. Processed: {}, Alerts created: {}",
            priority, processedCount, createdAlerts.size());

        return processedCount;
    }

    /**
     * Process availability check for all priorities using batch processing.
     *
     * @return total number of items processed
     */
    public int processAllAvailabilityChecks() {
        LOG.info("Starting full availability monitoring batch process");

        int totalProcessed = 0;

        // Process priorities 1-3
        for (int priority = 1; priority <= 3; priority++) {
            try {
                int processed = processAvailabilityForPriority(priority);
                totalProcessed += processed;
            } catch (Exception e) {
                LOG.error("Error processing priority {}: {}", priority, e.getMessage());
            }
        }

        LOG.info("Completed full availability monitoring. Total processed: {}", totalProcessed);
        return totalProcessed;
    }

    /**
     * Process watchlist item availability check without immediately sending notifications.
     * Returns the created Alert entity for later consolidated processing.
     *
     * @param item the watchlist item to check
     * @return the created Alert entity or null if no alert was created
     */
    private Alert processWatchlistItemWithoutNotification(WatchlistItem item) {
        LOG.debug("Processing watchlist item: {} for product: {}", item.getId(), item.getProduct().getName());

        // Check current availability from supplier API
        Optional<Boolean> currentAvailabilityOpt = Optional.of(true);
        // Optional<Boolean> currentAvailabilityOpt = supplierApiService.checkProductAvailability(item.getProduct());

        if (currentAvailabilityOpt.isEmpty()) {
            LOG.warn("Could not determine availability for product: {} (item: {})",
                item.getProduct().getName(), item.getId());
            //return null;
        }

        boolean currentAvailability = true;
        //
        Boolean lastAvailability = false;

        // Update last check timestamp
        item.setLastAvailabilityCheck(ZonedDateTime.now());

        // Check if availability has changed
        boolean availabilityChanged = false;

        if (availabilityChanged) {
            LOG.info("Availability changed for product: {} (item: {}) - {} -> {}",
                item.getProduct().getName(), item.getId(), lastAvailability, currentAvailability);

            // Update stored availability status
            item.setLastAvailabilityStatus(currentAvailability);
            watchlistItemRepository.save(item);

            // Create alert for availability change
            AlertDTO alert = alertService.createProductAvailabilityAlert(item, currentAvailability);
            LOG.debug("Created alert: {} for availability change", alert.getId());

            // Return the alert entity for later consolidated notification processing
            return alertService.findByAlertId(alert.getId()).orElse(null);
        } else {
            LOG.debug("No availability change for product: {} (item: {})", item.getProduct().getName(), item.getId());
            // Still update the item to record the check timestamp
            watchlistItemRepository.save(item);
            return null;
        }
    }

    /**
     * Find watchlist items that need monitoring for a specific priority.
     *
     * @param priority the priority level
     * @return list of watchlist items to monitor
     */
    private List<WatchlistItem> findWatchlistItemsForMonitoring(int priority) {
        // Find items with:
        // - alertEnabled = true
        // - priority = specified priority
        // - watchlist belongs to active pharmacy
        return watchlistItemRepository.findByAlertEnabledTrueAndPriorityAndWatchlistPharmacyActiveTrue(priority);
    }

    /**
     * Find watchlist items that need monitoring for a specific pharmacy.
     *
     * @param pharmacyId the pharmacy ID
     * @return list of watchlist items to monitor for this pharmacy
     */
    private List<WatchlistItem> findWatchlistItemsForPharmacy(Long pharmacyId) {
        // Find items with:
        // - alertEnabled = true
        // - watchlist belongs to the specified pharmacy
        // - pharmacy is active
        return watchlistItemRepository.findByAlertEnabledTrueAndWatchlistPharmacyIdAndWatchlistPharmacyActiveTrue(pharmacyId);
    }

    /**
     * Process availability monitoring for all watchlist items of a specific pharmacy.
     * Regardless of priority, processes all alert-enabled items for the pharmacy.
     *
     * @param pharmacyId the pharmacy ID to process
     * @return PharmacyMonitoringResult containing processing statistics
     */
    public PharmacyMonitoringResult processAvailabilityForPharmacy(Long pharmacyId) {
        LOG.info("Starting availability monitoring for pharmacy: {}", pharmacyId);

        List<WatchlistItem> items = findWatchlistItemsForPharmacy(pharmacyId);
        LOG.debug("Found {} watchlist items for pharmacy {} for monitoring", items.size(), pharmacyId);

        int processedCount = 0;
        List<Alert> createdAlerts = new ArrayList<>();

        for (WatchlistItem item : items) {
            try {
                Alert alert = processWatchlistItemWithoutNotification(item);
                if (alert != null) {
                    createdAlerts.add(alert);
                }
                processedCount++;
            } catch (Exception e) {
                LOG.error("Error processing watchlist item: {} for product: {} in pharmacy: {}",
                    item.getId(), item.getProduct().getName(), pharmacyId, e);
            }
        }

        // Send consolidated notifications for all created alerts
        if (!createdAlerts.isEmpty()) {
            notificationService.createConsolidatedEmailNotifications(createdAlerts);
        }

        LOG.info("Completed availability monitoring for pharmacy: {}. Processed: {}, Alerts created: {}",
            pharmacyId, processedCount, createdAlerts.size());

        return new PharmacyMonitoringResult(pharmacyId, processedCount, createdAlerts.size());
    }

    /**
     * Get monitoring statistics.
     *
     * @return monitoring statistics
     */
    public MonitoringStats getMonitoringStats() {
        long totalMonitoredItems = watchlistItemRepository.countByAlertEnabledTrueAndWatchlistPharmacyActiveTrue();

        // Count items by priority
        MonitoringStats stats = new MonitoringStats();
        stats.setTotalMonitoredItems(totalMonitoredItems);

        for (int priority = 1; priority <= 3; priority++) {
            long count = watchlistItemRepository.countByAlertEnabledTrueAndPriorityAndWatchlistPharmacyActiveTrue(priority);
            stats.addPriorityCount(priority, count);
        }

        return stats;
    }

    /**
     * Statistics for monitoring operations.
     */
    public static class MonitoringStats {
        private long totalMonitoredItems;
        private java.util.Map<Integer, Long> priorityCounts = new java.util.HashMap<>();

        public long getTotalMonitoredItems() {
            return totalMonitoredItems;
        }

        public void setTotalMonitoredItems(long totalMonitoredItems) {
            this.totalMonitoredItems = totalMonitoredItems;
        }

        public java.util.Map<Integer, Long> getPriorityCounts() {
            return priorityCounts;
        }

        public void setPriorityCounts(java.util.Map<Integer, Long> priorityCounts) {
            this.priorityCounts = priorityCounts;
        }

        public void addPriorityCount(int priority, long count) {
            this.priorityCounts.put(priority, count);
        }

        @Override
        public String toString() {
            return "MonitoringStats{" +
                "totalMonitoredItems=" + totalMonitoredItems +
                ", priorityCounts=" + priorityCounts +
                '}';
        }
    }

    /**
     * Result of pharmacy monitoring operations.
     */
    public static class PharmacyMonitoringResult {
        private Long pharmacyId;
        private int processedItems;
        private int alertsCreated;

        public PharmacyMonitoringResult() {}

        public PharmacyMonitoringResult(Long pharmacyId, int processedItems, int alertsCreated) {
            this.pharmacyId = pharmacyId;
            this.processedItems = processedItems;
            this.alertsCreated = alertsCreated;
        }

        public Long getPharmacyId() {
            return pharmacyId;
        }

        public void setPharmacyId(Long pharmacyId) {
            this.pharmacyId = pharmacyId;
        }

        public int getProcessedItems() {
            return processedItems;
        }

        public void setProcessedItems(int processedItems) {
            this.processedItems = processedItems;
        }

        public int getAlertsCreated() {
            return alertsCreated;
        }

        public void setAlertsCreated(int alertsCreated) {
            this.alertsCreated = alertsCreated;
        }

        @Override
        public String toString() {
            return "PharmacyMonitoringResult{" +
                "pharmacyId=" + pharmacyId +
                ", processedItems=" + processedItems +
                ", alertsCreated=" + alertsCreated +
                '}';
        }
    }
}
