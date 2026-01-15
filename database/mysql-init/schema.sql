-- RewardPlus Loyalty Program Database Schema
-- MySQL 8.0 Compatible

-- Create Database
CREATE DATABASE IF NOT EXISTS loyalty_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE loyalty_db;

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    date_of_birth DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    tier VARCHAR(20) DEFAULT 'BRONZE',
    gender VARCHAR(10),
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    occupation VARCHAR(50),
    company VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    enrollment_date DATE,
    last_activity_date DATE,
    preferences TEXT,
    profile_image_url VARCHAR(255),
    
    INDEX idx_customer_email (email),
    INDEX idx_customer_code (customer_code),
    INDEX idx_customer_status (status),
    INDEX idx_customer_tier (tier),
    INDEX idx_customer_city (city),
    INDEX idx_customer_enrollment (enrollment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_code VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    discount_applied DECIMAL(10, 2) DEFAULT 0.00,
    net_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_type VARCHAR(30) NOT NULL DEFAULT 'PURCHASE',
    status VARCHAR(30) DEFAULT 'COMPLETED',
    store_code VARCHAR(50),
    store_name VARCHAR(100),
    cashier_code VARCHAR(50),
    cashier_name VARCHAR(100),
    product_category VARCHAR(50),
    product_details TEXT,
    payment_method VARCHAR(100),
    receipt_number VARCHAR(50),
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_transaction_customer (customer_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_transaction_code (transaction_code),
    INDEX idx_transaction_status (status),
    INDEX idx_transaction_store (store_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Loyalty Points Table
CREATE TABLE IF NOT EXISTS loyalty_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE,
    transaction_id BIGINT,
    points_earned BIGINT NOT NULL DEFAULT 0,
    points_redeemed BIGINT NOT NULL DEFAULT 0,
    points_expired BIGINT NOT NULL DEFAULT 0,
    points_adjusted BIGINT NOT NULL DEFAULT 0,
    current_balance BIGINT NOT NULL DEFAULT 0,
    lifetime_points BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_earned_date DATETIME,
    last_redeemed_date DATETIME,
    last_adjusted_date DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    points_expiration_date DATETIME,
    notes VARCHAR(500),
    
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL,
    INDEX idx_loyalty_customer (customer_id),
    INDEX idx_loyalty_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Rewards Table
CREATE TABLE IF NOT EXISTS rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    reward_code VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL DEFAULT 'DISCOUNT',
    category VARCHAR(30) NOT NULL DEFAULT 'PRODUCT',
    points_required BIGINT NOT NULL,
    discount_percentage DECIMAL(5, 2),
    discount_amount DECIMAL(10, 2),
    cash_value DECIMAL(10, 2),
    image_url VARCHAR(255),
    terms_and_conditions TEXT,
    quantity INT DEFAULT 0,
    quantity_redeemed INT DEFAULT 0,
    quantity_per_customer INT DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    start_date DATE,
    expiry_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_date DATETIME,
    redemption_instructions VARCHAR(500),
    vendor_name VARCHAR(100),
    vendor_code VARCHAR(50),
    applicable_stores VARCHAR(50),
    minimum_purchase_amount INT,
    
    INDEX idx_reward_code (reward_code),
    INDEX idx_reward_category (category),
    INDEX idx_reward_status (status),
    INDEX idx_reward_expiry (expiryDate),
    INDEX idx_reward_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Promotions Table
CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    promotion_code VARCHAR(50) NOT NULL UNIQUE,
    promotion_type VARCHAR(30) NOT NULL DEFAULT 'DISCOUNT',
    status VARCHAR(30) DEFAULT 'DRAFT',
    start_date DATE,
    end_date DATE,
    discount_percentage DECIMAL(5, 2),
    discount_amount DECIMAL(10, 2),
    bonus_points_multiplier DECIMAL(5, 2),
    bonus_points_fixed INT,
    minimum_purchase_amount DECIMAL(10, 2),
    maximum_discount DECIMAL(10, 2),
    usage_limit INT DEFAULT 0,
    usage_count INT DEFAULT 0,
    usage_limit_per_customer INT DEFAULT 1,
    minimum_tier VARCHAR(20),
    minimum_age INT,
    maximum_age INT,
    target_gender VARCHAR(10),
    target_occupation VARCHAR(50),
    target_city VARCHAR(100),
    target_state VARCHAR(100),
    target_segment_description VARCHAR(500),
    minimum_lifetime_spend INT,
    minimum_transactions INT,
    target_product_category VARCHAR(100),
    target_customer_ids VARCHAR(255),
    exclusive_to_new_customers BOOLEAN DEFAULT FALSE,
    terms_and_conditions TEXT,
    image_url VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    INDEX idx_promotion_code (promotion_code),
    INDEX idx_promotion_status (status),
    INDEX idx_promotion_dates (start_date, end_date),
    INDEX idx_promotion_type (promotion_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Customer-Rewards Junction Table (Many-to-Many)
CREATE TABLE IF NOT EXISTS customer_rewards (
    customer_id BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    redeemed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (customer_id, reward_id),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (reward_id) REFERENCES rewards(id) ON DELETE CASCADE,
    INDEX idx_customer_reward_customer (customer_id),
    INDEX idx_customer_reward_reward (reward_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Promotion-Customers Junction Table (Many-to-Many)
CREATE TABLE IF NOT EXISTS promotion_customers (
    promotion_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (promotion_id, customer_id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_promo_cust_promo (promotion_id),
    INDEX idx_promo_cust_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Redemption Logs Table
CREATE TABLE IF NOT EXISTS redemption_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    redemption_code VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    points_redeemed BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    channel VARCHAR(30) DEFAULT 'ONLINE',
    redemption_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATETIME,
    fulfillment_date DATETIME,
    used_date DATETIME,
    store_code VARCHAR(100),
    store_name VARCHAR(100),
    cashier_code VARCHAR(50),
    processed_by VARCHAR(100),
    redemption_code_generated VARCHAR(500),
    voucher_code VARCHAR(255),
    redemption_url TEXT,
    notes TEXT,
    cancellation_reason TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (reward_id) REFERENCES rewards(id) ON DELETE CASCADE,
    INDEX idx_redemption_customer (customer_id),
    INDEX idx_redemption_reward (reward_id),
    INDEX idx_redemption_date (redemptionDate),
    INDEX idx_redemption_code (redemptionCode),
    INDEX idx_redemption_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

