# Product Availability Monitoring Workflow

## Overview

The product availability monitoring system automatically tracks product availability changes for pharmacy watchlists and sends real-time notifications when products become available or unavailable.

## How It Works

### 1. Watchlist Setup
- Pharmacies add products to their watchlists through the application
- Each watchlist item can be assigned a priority level (1-3) that determines monitoring frequency
- Items must have "Alert Enabled" turned on to participate in monitoring

### 2. Priority-Based Monitoring Schedule
The system monitors products at different intervals based on their priority:

- **Priority 1 (Critical)**: Checked every minute
- **Priority 2 (Important)**: Checked every 30 minutes
- **Priority 3 (Standard)**: Checked every hour

### 3. Availability Detection Process
1. **Scheduled Check**: The system automatically runs availability checks based on priority schedules
2. **Supplier Query**: For each watchlist item, the system contacts the supplier's API to get current availability status
3. **Change Detection**: The system compares the current availability with the last known status
4. **Status Update**: The watchlist item's availability status and check timestamp are updated

### 4. Alert Creation and Notification
When availability changes are detected:

1. **Alert Generation**: An automatic alert is created documenting the availability change
2. **Email Notification**: All users associated with the pharmacy receive email notifications
3. **Status Tracking**: Email delivery status is tracked through webhook integration

### 5. Management and Control
Administrators can:
- View monitoring statistics and scheduler status
- Manually trigger availability checks for specific priorities
- Start/stop monitoring schedules as needed
- Restart the entire monitoring system

## Benefits

- **Real-time Updates**: Critical products are monitored every minute
- **Efficient Resource Usage**: Lower priority items are checked less frequently
- **Automatic Notifications**: No manual checking required - users are notified immediately
- **Comprehensive Tracking**: Full audit trail of availability changes and notifications
- **Flexible Control**: Administrators can adjust monitoring as needed

## User Experience

1. **Setup**: Add products to watchlist and set appropriate priority levels
2. **Automatic Monitoring**: System runs in background without user intervention
3. **Instant Alerts**: Receive email notifications when product availability changes
4. **History Tracking**: View complete alert history for each watchlist item