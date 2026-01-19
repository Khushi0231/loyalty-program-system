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
CREATE TABLE rewards (
    reward_id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    points_required INT NOT NULL
);

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

-- Redemption Log Table
CREATE TABLE redemption_log (
    redemption_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    reward_id INT,
    redeemed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (reward_id) REFERENCES rewards(reward_id)
);

-- Add more tables and relationships as necessary for the loyalty program system.
