-- MySQL Schema for Loyalty Program System

-- Customers Table
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(15),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Transactions Table
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Loyalty Points Table
CREATE TABLE loyalty_points (
    point_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    points INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Rewards Table
<<<<<<< Updated upstream
CREATE TABLE rewards (
    reward_id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    points_required INT NOT NULL
);
=======
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
    INDEX idx_reward_expiry (expiry_date),
    INDEX idx_reward_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
>>>>>>> Stashed changes

-- Promotions Table
CREATE TABLE promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL
);

-- Customer Rewards Table
CREATE TABLE customer_rewards (
    customer_reward_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    reward_id INT,
    redeemed_at DATETIME,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (reward_id) REFERENCES rewards(reward_id)
);

<<<<<<< Updated upstream
-- Redemption Log Table
CREATE TABLE redemption_log (
    redemption_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    reward_id INT,
    redeemed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (reward_id) REFERENCES rewards(reward_id)
);
=======
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
    INDEX idx_redemption_date (redemption_date),
    INDEX idx_redemption_code (redemption_code),
    INDEX idx_redemption_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
>>>>>>> Stashed changes

-- Add more tables and relationships as necessary for the loyalty program system.
