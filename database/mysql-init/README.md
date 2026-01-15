# RewardPlus Loyalty Program - Database

MySQL database initialization scripts for the loyalty program.

## Files

- **schema.sql** - Database schema (DDL) for all tables
- **seed-data.sql** - Demo data for testing and development
- **Dockerfile** - MySQL container with automatic initialization

## Quick Start

### Using Docker

```bash
# Build and run MySQL container
docker build -t rewardplus/mysql:latest .
docker run -d \
  --name loyalty-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=password \
  rewardplus/mysql:latest
```

### Using Docker Compose

```yaml
version: '3.8'
services:
  mysql:
    build: ./database/mysql-init
    container_name: loyalty-mysql
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: loyalty_db
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - loyalty-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-ppassword"]
      interval: 10s
      timeout: 5s
      retries: 5

networks:
  loyalty-network:
    driver: bridge

volumes:
  mysql-data:
```

### Manual Setup

1. Create database:
```sql
CREATE DATABASE loyalty_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Run schema:
```bash
mysql -u root -p loyalty_db < schema.sql
```

3. Load seed data:
```bash
mysql -u root -p loyalty_db < seed-data.sql
```

## Database Schema

### Tables

| Table | Description |
|-------|-------------|
| `customers` | Customer information and profile |
| `transactions` | Purchase transactions |
| `loyalty_points` | Points balance and history |
| `rewards` | Available rewards catalog |
| `promotions` | Marketing promotions |
| `customer_rewards` | Junction table for redeemed rewards |
| `promotion_customers` | Junction table for targeted promotions |
| `redemption_logs` | Redemption history |

### Customer Tiers

| Tier | Description |
|------|-------------|
| BRONZE | Default tier for new members |
| SILVER | After 1,000 lifetime points |
| GOLD | After 5,000 lifetime points |
| PLATINUM | After 10,000 lifetime points |
| DIAMOND | After 25,000 lifetime points |

## Seed Data

The seed data includes:

- **8 demo customers** across all tiers
- **8 demo rewards** with various point costs
- **8 demo promotions** with different targeting criteria
- **8 demo transactions** for testing
- **Points balances** for all customers
- **Sample redemptions**

## Connection Settings

| Setting | Value |
|---------|-------|
| Host | localhost |
| Port | 3306 |
| Database | loyalty_db |
| Username | root |
| Password | password |

## JDBC Connection String

```
jdbc:mysql://localhost:3306/loyalty_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

