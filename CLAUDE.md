# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Starting the Application
```bash
# Start development server (default dev profile with PostgreSQL)
./mvnw

# Start with debug mode (port 8000)
npm run backend:debug

# Start application only
npm run app:start
```

### Database Management
```bash
# Start PostgreSQL container
npm run docker:db:up

# Stop PostgreSQL container
npm run docker:db:down

# Start all services (database and others)
npm run services:up

# Apply database migrations
./mvnw liquibase:update
```

### Testing
```bash
# Run all tests (unit + integration)
./mvnw verify

# Unit tests only (faster, no database)
npm run backend:unit:test

# Full CI test suite (includes documentation and code quality)
npm run ci:backend:test
```

### Building
```bash
# Development JAR
npm run java:jar:dev

# Production JAR
npm run java:jar:prod
# or: ./mvnw -Pprod clean verify

# WAR for application servers
npm run java:war:prod

# Docker image
npm run java:docker
```

### Code Quality
```bash
# Code style check
./mvnw checkstyle:check

# Start SonarQube server
docker compose -f src/main/docker/sonar.yml up -d

# Run SonarQube analysis
./mvnw -Pprod clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

## Architecture Overview

**Technology Stack**: JHipster 8.11.0 monolithic Spring Boot application
- **Java 17** with Spring Boot 3.4.5
- **PostgreSQL 17** (dev: localhost:5432, user: postgres/postgres)
- **JWT Authentication** with Spring Security
- **Hazelcast** distributed caching
- **Liquibase** database migrations
- **MapStruct** for DTO mapping

**Layer Architecture**:
- **Web Layer**: `src/main/java/com/pharmaresolve/medcom/web/rest/` - REST controllers
- **Service Layer**: `src/main/java/com/pharmaresolve/medcom/service/` - Business logic
- **Repository Layer**: `src/main/java/com/pharmaresolve/medcom/repository/` - Data access (Spring Data JPA)
- **Domain Layer**: `src/main/java/com/pharmaresolve/medcom/domain/` - JPA entities

**Key Domain Entities**:
- **Pharmacy**: Pharmacy management
- **Product**: Product catalog
- **Watchlist**: User watchlists
- **WatchlistItem**: Items in watchlists
- **Alert**: Alert system
- **Notification**: Notification management
- **User/Authority**: Authentication and authorization

**Maven Profiles**:
- `dev` (default): Development with PostgreSQL, DevTools enabled
- `prod`: Production build with optimizations
- `war`: WAR packaging for application servers
- `docker-compose`: Docker integration

**Testing Strategy**:
- **Unit Tests**: `*Test.java` files using JUnit 5
- **Integration Tests**: `*IT.java` files with `@IntegrationTest` annotation
- **Testcontainers**: PostgreSQL integration testing
- **ArchUnit**: Architectural rule testing

**Configuration Files**:
- `application.yml`: Base configuration
- `application-dev.yml`: Development profile (PostgreSQL, logging)
- `application-prod.yml`: Production profile (optimized settings)
- `master.xml`: Liquibase migrations

**API Patterns**:
- RESTful services with `/api/*` endpoints
- JWT authentication via `/api/authenticate`
- Management endpoints via `/management/*`
- OpenAPI/Swagger documentation
- Standardized error handling and validation

**Docker Integration**:
- All services (Database): `docker compose -f ./docker-compose.yml up -d`
