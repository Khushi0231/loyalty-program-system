# RewardPlus Loyalty Backend

Enterprise Customer Loyalty Program System - Backend API

## Overview

This is the backend service for the RewardPlus Loyalty Program, built with:
- **Spring Boot 3.2**
- **Java 17**
- **Spring Data JPA**
- **MySQL**
- **Swagger/OpenAPI**
- **Lombok**
- **ModelMapper**

## Features

### Customer Management
- Customer enrollment with welcome bonus points
- Customer profile management
- Customer search and segmentation
- Tier management (Bronze, Silver, Gold, Platinum, Diamond)

### Transaction Processing
- Purchase transaction recording
- Automatic loyalty points calculation
- Transaction history and查询

### Loyalty Points
- Points balance management
- Points redemption
- Points adjustment (manual)
- Points expiration tracking

### Reward Management
- Reward catalog management
- Reward redemption processing
- Redemption tracking and history

### Marketing & Promotions
- Campaign creation and management
- Customer segmentation targeting
- Promotion assignment to customer groups
- Bonus points multipliers

### Analytics & Reporting
- Program summary metrics
- Customer activity analytics
- Redemption trends
- Sales analytics
- Tier distribution

## API Documentation

Once the application is running, access Swagger UI at:
```
http://localhost:8080/api/swagger-ui.html
```

OpenAPI documentation available at:
```
http://localhost:8080/api/api-docs
```

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Configuration

The application uses `application.yml` for configuration. Key settings:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/loyalty_db
    username: root
    password: password

app:
  points:
    earn-rate: 10        # Points per dollar
    welcome-bonus: 100   # Welcome bonus points
```

### Local Development

1. Create MySQL database:
```sql
CREATE DATABASE loyalty_db;
```

2. Configure database connection in `application.yml`

3. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

### Docker Build

```bash
# Build the image
docker build -t rewardplus/loyalty-backend:latest .

# Run the container
docker run -p 8080:8080 \
  -e DB_HOST=mysql-host \
  -e DB_PORT=3306 \
  -e DB_NAME=loyalty_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  rewardplus/loyalty-backend:latest
```

### Docker Compose

```yaml
version: '3.8'
services:
  loyalty-backend:
    build: ./backend/loyalty-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=mysql
      - DB_PORT=3306
    depends_on:
      - mysql
    networks:
      - loyalty-network

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: loyalty_db
    volumes:
      - mysql-data:/var/lib/mysql
      - ./database/mysql-init/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    networks:
      - loyalty-network

networks:
  loyalty-network:
    driver: bridge

volumes:
  mysql-data:
```

## API Endpoints

### Customer APIs
- `POST /api/v1/customers/enroll` - Enroll new customer
- `GET /api/v1/customers/{id}` - Get customer by ID
- `GET /api/v1/customers` - List all customers (paginated)
- `PUT /api/v1/customers/{id}` - Update customer
- `GET /api/v1/customers/{id}/points` - Get customer points

### Transaction APIs
- `POST /api/v1/transactions` - Record transaction
- `GET /api/v1/transactions/{id}` - Get transaction
- `GET /api/v1/transactions/customer/{customerId}` - Customer transactions

### Reward APIs
- `POST /api/v1/rewards` - Create reward
- `GET /api/v1/rewards/available` - Get available rewards
- `POST /api/v1/rewards/redeem` - Redeem reward

### Promotion APIs
- `POST /api/v1/promotions` - Create promotion
- `GET /api/v1/promotions/active` - Get active promotions
- `POST /api/v1/promotions/{id}/activate` - Activate promotion

### Analytics APIs
- `GET /api/v1/analytics/summary` - Program summary
- `GET /api/v1/analytics/customers` - Customer analytics
- `GET /api/v1/analytics/redemptions` - Redemption trends

## Project Structure

```
src/main/java/com/rewardplus/loyalty/
├── config/
│   ├── AppConfig.java
│   ├── SwaggerConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── CustomerController.java
│   ├── TransactionController.java
│   ├── RewardController.java
│   ├── PromotionController.java
│   └── AnalyticsController.java
├── dto/
│   ├── CustomerDTO.java
│   ├── TransactionDTO.java
│   ├── LoyaltyPointsDTO.java
│   ├── RewardDTO.java
│   ├── PromotionDTO.java
│   ├── RedemptionDTO.java
│   └── ApiResponse.java
├── entity/
│   ├── Customer.java
│   ├── Transaction.java
│   ├── LoyaltyPoints.java
│   ├── Reward.java
│   ├── Promotion.java
│   └── RedemptionLog.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   ├── InsufficientPointsException.java
│   ├── DuplicateResourceException.java
│   ├── GlobalExceptionHandler.java
│   └── ErrorResponse.java
├── repository/
│   ├── CustomerRepository.java
│   ├── TransactionRepository.java
│   ├── LoyaltyPointsRepository.java
│   ├── RewardRepository.java
│   ├── PromotionRepository.java
│   └── RedemptionLogRepository.java
└── service/
    ├── CustomerService.java
    ├── TransactionService.java
    ├── LoyaltyPointsService.java
    ├── RewardService.java
    ├── RedemptionService.java
    ├── PromotionService.java
    └── AnalyticsService.java
```

## Testing

```bash
# Run unit tests
mvn test

# Run with coverage report
mvn test jacoco:report
```

## License

Copyright 2024 RewardPlus Retailers. All rights reserved.

