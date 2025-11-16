package com.oussamabenberkane.medcom.service;

import com.oussamabenberkane.medcom.domain.Notification;
import com.oussamabenberkane.medcom.domain.User;
import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.domain.WatchlistItem;
import com.oussamabenberkane.medcom.domain.enumeration.NotificationType;
import com.oussamabenberkane.medcom.repository.NotificationRepository;
import com.oussamabenberkane.medcom.repository.UserRepository;
import com.oussamabenberkane.medcom.repository.WatchlistItemRepository;
import com.oussamabenberkane.medcom.service.external.SupplierApiService;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for monitoring product availability.
 */
@Service
@Transactional
public class ProductAvailabilityMonitoringService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAvailabilityMonitoringService.class);

    private final WatchlistItemRepository watchlistItemRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SupplierApiService supplierApiService;
    private final MailService mailService;

    public ProductAvailabilityMonitoringService(
        WatchlistItemRepository watchlistItemRepository,
        NotificationRepository notificationRepository,
        UserRepository userRepository,
        SupplierApiService supplierApiService,
        MailService mailService
    ) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.supplierApiService = supplierApiService;
        this.mailService = mailService;
    }

    /**
     * Check availability for all watchlist items.
     * Called by the scheduler.
     */
    public void checkAllWatchlistItems() {
        LOG.debug("Starting availability check for all watchlist items");

        List<WatchlistItem> watchlistItems = watchlistItemRepository.findAll();
        LOG.info("Checking availability for {} watchlist items", watchlistItems.size());

        int checkedCount = 0;
        int changedCount = 0;
        int errorCount = 0;

        for (WatchlistItem item : watchlistItems) {
            try {
                boolean changed = checkAndUpdateAvailability(item);
                if (changed) {
                    changedCount++;
                }
                checkedCount++;
            } catch (Exception e) {
                // KISS principle: skip items with errors
                LOG.error("Error checking availability for watchlist item {}: {}", item.getId(), e.getMessage());
                errorCount++;
            }
        }

        LOG.info("Availability check completed. Checked: {}, Changed: {}, Errors: {}", checkedCount, changedCount, errorCount);
    }

    /**
     * Check and update availability for a specific watchlist item.
     *
     * @param item The watchlist item to check
     * @return true if availability changed, false otherwise
     */
    public boolean checkAndUpdateAvailability(WatchlistItem item) {
        if (item.getProduct() == null || item.getProduct().getCode() == null) {
            LOG.warn("Watchlist item {} has no product or product code", item.getId());
            return false;
        }

        String productCode = item.getProduct().getCode();
        LOG.debug("Checking availability for product: {}", productCode);

        try {
            Map<String, Object> availabilityData = supplierApiService.checkProductAvailability(productCode);

            if (availabilityData.containsKey("error")) {
                // KISS: Skip items with supplier errors
                LOG.warn("Supplier API error for product {}: {}", productCode, availabilityData.get("error"));
                return false;
            }

            Boolean currentAvailability = (Boolean) availabilityData.get("available");
            Boolean previousAvailability = item.getLastAvailabilityStatus();

            // Update the watchlist item
            item.setLastAvailabilityStatus(currentAvailability);
            item.setDateUpdated(Instant.now());

            // Check if availability changed
            if (previousAvailability != null && !previousAvailability.equals(currentAvailability)) {
                item.setLastAvailabilityChange(ZonedDateTime.now());
                watchlistItemRepository.save(item);

                LOG.info("Availability changed for product {}: {} -> {}", productCode, previousAvailability, currentAvailability);

                // Generate notifications for all users in the pharmacy
                generateNotificationsForWatchlist(item);

                return true;
            } else {
                watchlistItemRepository.save(item);
                LOG.debug("No availability change for product {}", productCode);
            }
        } catch (Exception e) {
            LOG.error("Error checking availability for product {}: {}", productCode, e.getMessage());
            throw e;
        }

        return false;
    }

    /**
     * Generate notifications for all users in the pharmacy when a watchlist item availability changes.
     *
     * @param watchlistItem The watchlist item that changed
     */
    private void generateNotificationsForWatchlist(WatchlistItem watchlistItem) {
        Watchlist watchlist = watchlistItem.getWatchlist();
        if (watchlist == null || watchlist.getPharmacy() == null) {
            LOG.warn("Watchlist item {} has no watchlist or pharmacy", watchlistItem.getId());
            return;
        }

        Long pharmacyId = watchlist.getPharmacy().getId();
        List<User> pharmacyUsers = userRepository.findAllByPharmacyId(pharmacyId);

        LOG.info("Generating notifications for {} users in pharmacy {}", pharmacyUsers.size(), pharmacyId);

        for (User user : pharmacyUsers) {
            Notification notification = new Notification();
            notification.setCreatedAt(Instant.now());
            notification.setNotificationType(NotificationType.AVAILABILITY_CHANGE);
            notification.setMessage(
                String.format(
                    "Product '%s' availability changed to: %s",
                    watchlistItem.getProduct().getName(),
                    watchlistItem.getLastAvailabilityStatus() ? "Available" : "Not Available"
                )
            );
            notification.setWatchlist(watchlist);
            notification.setUser(user);
            notification.setPharmacy(watchlist.getPharmacy());
            notification.setWatchlistItem(watchlistItem);
            notification.setEmailSent(false);

            notificationRepository.save(notification);
            LOG.debug("Created notification {} for user {}", notification.getId(), user.getLogin());
        }
    }

    /**
     * Check availability for a specific pharmacy's watchlist (manual trigger).
     *
     * @param pharmacyId The pharmacy ID
     * @return Number of items checked
     */
    public int checkPharmacyWatchlist(Long pharmacyId) {
        LOG.info("Manual availability check triggered for pharmacy {}", pharmacyId);

        List<WatchlistItem> items = watchlistItemRepository.findAllByWatchlistPharmacyId(pharmacyId);
        LOG.info("Found {} watchlist items for pharmacy {}", items.size(), pharmacyId);

        int checkedCount = 0;
        for (WatchlistItem item : items) {
            try {
                checkAndUpdateAvailability(item);
                checkedCount++;
            } catch (Exception e) {
                LOG.error("Error checking item {}: {}", item.getId(), e.getMessage());
            }
        }

        return checkedCount;
    }
}
