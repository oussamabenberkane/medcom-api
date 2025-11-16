# MedCom API Development Prompt

## Project Overview

You are developing a pharmacy product monitoring system called MedCom that helps pharmacists monitor product availability and react quickly to shortages. The system allows pharmacists to maintain personalized watchlists and receive email alerts when watched products' availability changes at linked suppliers.

## Architecture Requirements

### Core Entities

1. **Product**: id (PK), name, code (String unique identifier used in API calls to supplier), officialUrl, created, createdBy, updated, updatedBy
2. **Pharmacy**: id (PK), name, address, email, phone, website, active, createdAt, createdBy, activatedBy, deactivatedBy, deleted, deletedBy
3. **User**: Many to one with pharmacy, linked to pharmacy by admin, login format: "{firstname}.{lastname}.{id}"
4. **Watchlist**: One-to-one with pharmacy
5. **WatchlistItem**: id, dateAdded, addedBy, dateUpdated, updatedBy, lastAvailabilityStatus (Boolean), lastAvailabilityChange (ZonedDateTime), #productId
6. **Notification**: Many to one with watchlist, user, pharmacy; contains #watchlistItemId, #userId, #pharmacyId, and email status tracking fields

### Business Logic Requirements

- Users can only update products they created
- One watchlist per pharmacy, limited to 10 items by default
- Job runs every minute (configurable) to check availability of products that appear in at least one watchlistItem
- If there are errors from suppliers during availability checks, skip that job and continue with others
- If any product in a watchlist changes availability, notifications are generated for all users in the pharmacy
- Notifications are sent via MailJet
- When users add new products, they become immediately available to all users
- Users can only update products they created (the updatedBy field is just a String containing the login of that user)
- Pharmacies are automatically active when created by admin
- Job checks availability of products that appear in at least one watchlistItem (not all products)

### Technical Stack

- Spring Boot with Java 17
- PostgreSQL database
- REST API architecture
- JWT for authentication
- Thymeleaf for email templates
- MailJet for email delivery
- JHipster framework

### Role-Based Authorization

- Two authorities: Admin and User
- Two frontend apps: one for admin, one for user
- Backend is centralized with role-based authorization

## Reusable Components from Old Project

Based on the existing MedComApiOld project, the following components should be carried forward and adapted:

### Email System

- **MailService.java**: Contains well-structured email functionality including alert email methods
- **Email templates**:
  - `alertEmail.html` - Email template for individual product alerts
  - `consolidatedAlertEmail.html` - Email template for multiple product alerts
  - `activationEmail.html` - User activation emails
  - `passwordResetEmail.html` - Password reset emails
- **EmailDeliveryService.java**: Handles email delivery with status tracking

### Core Services

- **ProductAvailabilityMonitoringService.java**: Core monitoring logic that checks product availability
- **SupplierApiService.java**: Service for checking product availability from external supplier API
- **NotificationService.java**: Comprehensive notification management
- **RestTemplateConfiguration.java**: For making external API calls
- **EmailAlreadyUsedException.java**, **InvalidPasswordException.java**, **UsernameAlreadyUsedException.java**: Exception handling

### Security Configuration

- **SecurityConfiguration.java** and **SecurityJwtConfiguration.java**: JWT-based authentication
- **AuthoritiesConstants.java**: Role definitions
- **DomainUserDetailsService.java**: User authentication details

### Data Access Layer

- **Repository interfaces**: Well-defined repository patterns
- **EntityMapper/DTO infrastructure**: MapStruct-based object mapping
- **AbstractAuditingEntity.java**: Audit trail capabilities

### Scheduling & Monitoring

- **ProductAvailabilitySchedulerService.java**: Scheduling infrastructure (though the new architecture is simpler - checking all items every minute vs. priority-based in old version)

### Web Layer

- **Resource controllers**: Well-structured REST API endpoints
- **Exception handling**: Proper error handling with BadRequestAlertException

## Implementation Guidelines

### New Architecture Differences

The new architecture simplifies the notification system from the old project:

- The old project had a complex Alert -> Notification relationship with multiple entities
- The new architecture uses a simpler Notification entity that is many-to-one with watchlist, user, and pharmacy
- The job now runs every minute for all watchlist items that appear in at least one watchlist (not priority-based)
- Skip supplier API errors with KISS principle (don't check if there are errors from suppliers)

### Key Implementation Points

1. **Product Code Field**: Use the "code" field as the unique identifier for API calls to suppliers
2. **Availability Check**: Implement a placeholder method that returns a JSON with an "available" Boolean attribute
3. **Watchlist Limits**: Implement a default limit of 10 items per pharmacy's watchlist
4. **Manual Check API**: Implement an endpoint to check availability of a specific user's pharmacy's watchlist
5. **Configurable Job**: Make the monitoring job interval configurable

### Email Integration

- Use MailJet for email delivery
- Adapt existing email templates from the old project
- Include proper tracking of email delivery status in notifications

### Error Handling

- Implement graceful handling of supplier API errors
- Log errors appropriately without stopping the monitoring process
- Follow the KISS principle - skip items with supplier errors rather than failing the entire job

## Important Note

When implementing this system, please ask clarifying questions whenever you encounter undisclosed information or need further specifications. I want to ensure the implementation matches the exact requirements, so don't make assumptions about any details that aren't explicitly specified.
