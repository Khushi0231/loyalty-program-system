-- RewardPlus Loyalty Program Seed Data
-- MySQL 8.0 Compatible

USE loyalty_db;

-- Insert Demo Customers
INSERT INTO customers (customer_code, first_name, last_name, email, phone, date_of_birth, status, tier, gender, address, city, state, postal_code, country, occupation, company, enrollment_date, preferences) VALUES
('CUST000001', 'John', 'Smith', 'john.smith@email.com', '+1-555-0101', '1985-03-15', 'ACTIVE', 'GOLD', 'M', '123 Main Street', 'New York', 'NY', '10001', 'USA', 'Engineer', 'Tech Corp', '2023-01-15', 'prefers_email_notifications'),
('CUST000002', 'Jane', 'Doe', 'jane.doe@email.com', '+1-555-0102', '1990-07-22', 'ACTIVE', 'PLATINUM', 'F', '456 Oak Avenue', 'Los Angeles', 'CA', '90001', 'USA', 'Manager', 'Finance Inc', '2022-06-20', 'prefers_sms_notifications'),
('CUST000003', 'Robert', 'Johnson', 'robert.j@email.com', '+1-555-0103', '1978-11-30', 'ACTIVE', 'SILVER', 'M', '789 Pine Road', 'Chicago', 'IL', '60601', 'USA', 'Teacher', 'School District', '2023-03-10', NULL),
('CUST000004', 'Emily', 'Williams', 'emily.w@email.com', '+1-555-0104', '1995-05-05', 'ACTIVE', 'BRONZE', 'F', '321 Elm Street', 'Houston', 'TX', '77001', 'USA', 'Student', NULL, '2023-09-01', 'interested_in_electronics'),
('CUST000005', 'Michael', 'Brown', 'michael.b@email.com', '+1-555-0105', '1982-09-18', 'ACTIVE', 'DIAMOND', 'M', '654 Maple Drive', 'Phoenix', 'AZ', '85001', 'USA', 'Doctor', 'Medical Center', '2021-12-01', 'prefers_health_offers'),
('CUST000006', 'Sarah', 'Davis', 'sarah.d@email.com', '+1-555-0106', '1988-02-14', 'INACTIVE', 'BRONZE', 'F', '987 Cedar Lane', 'Philadelphia', 'PA', '19101', 'USA', 'Designer', 'Creative Agency', '2023-05-20', NULL),
('CUST000007', 'David', 'Wilson', 'david.w@email.com', '+1-555-0107', '1975-12-25', 'ACTIVE', 'GOLD', 'M', '147 Birch Boulevard', 'San Antonio', 'TX', '78201', 'USA', 'Business Owner', 'Wilson Enterprises', '2022-08-15', 'prefers_business_offers'),
('CUST000008', 'Lisa', 'Anderson', 'lisa.a@email.com', '+1-555-0108', '1992-08-08', 'ACTIVE', 'SILVER', 'F', '258 Walnut Way', 'San Diego', 'CA', '92101', 'USA', 'Nurse', 'Hospital', '2023-07-01', 'interested_in_family_products');

-- Insert Demo Rewards
INSERT INTO rewards (name, description, reward_code, type, category, points_required, discount_percentage, discount_amount, cash_value, quantity, quantity_per_customer, status, start_date, expiry_date, terms_and_conditions) VALUES
('10% Off Next Purchase', 'Get 10% discount on your next purchase', 'RWD10PCT', 'DISCOUNT', 'PRODUCT', 500, 10.00, NULL, NULL, 100, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'Valid for purchases over $50'),
('$25 Gift Card', 'Redeem for $25 gift card', 'RWD25GC', 'GIFT_CARD', 'PRODUCT', 2500, NULL, NULL, 25.00, 50, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'Gift card expires 30 days after redemption'),
('Free Product Sample', 'Choose a free product sample', 'FREESAMPLE', 'FREE_PRODUCT', 'PRODUCT', 200, NULL, NULL, 10.00, 200, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'While supplies last'),
('Double Points Day', 'Earn double points on your next purchase', 'DBLPOINTS', 'EXPERIENCE', 'EXPERIENCE', 300, NULL, NULL, NULL, 1000, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'Valid for one transaction'),
('$50 Cashback', 'Get $50 cashback on your account', 'CASHBACK50', 'CASHBACK', 'PRODUCT', 5000, NULL, NULL, 50.00, 20, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'Cashback credited within 7 days'),
('VIP Lounge Access', 'Access to VIP lounge during events', 'VIPLOUNGE', 'EXPERIENCE', 'EXPERIENCE', 10000, NULL, NULL, 100.00, 10, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'Valid for one event'),
('Free Shipping', 'Free standard shipping on next 5 orders', 'FREESHIP5', 'DISCOUNT', 'SERVICE', 400, NULL, NULL, 15.00, 500, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'Standard shipping only'),
('Birthday Bonus', 'Special birthday reward', 'BDAY2024', 'BONUS_POINTS', 'EXPERIENCE', 0, NULL, NULL, NULL, 1000, 1, 'ACTIVE', '2024-01-01', '2024-12-31', 'Requires birthday on file');

-- Insert Demo Promotions
INSERT INTO promotions (name, description, promotion_code, promotion_type, status, start_date, end_date, bonus_points_multiplier, bonus_points_fixed, minimum_purchase_amount, usage_limit, usage_limit_per_customer, minimum_tier, minimum_age, maximum_age, target_gender, exclusive_to_new_customers, terms_and_conditions, created_by) VALUES
('New Year Bonus', 'Double points for all purchases in January', 'NEWYEAR2024', 'DOUBLE_POINTS', 'ACTIVE', '2024-01-01', '2024-01-31', 2.00, NULL, NULL, 0, 1, NULL, NULL, NULL, NULL, FALSE, 'Applies to all transactions over $10', 'admin'),
('Spring Sale', '20% off + 2x points', 'SPRING2024', 'LOYALTY_BOOST', 'ACTIVE', '2024-03-01', '2024-03-31', 2.00, NULL, 50.00, 1000, 1, NULL, NULL, NULL, NULL, FALSE, 'Valid on select items', 'marketing_team'),
('Senior Discount', 'Special offer for senior citizens', 'SENIOR20', 'DISCOUNT', 'SCHEDULED', '2024-04-01', '2024-04-30', NULL, NULL, 25.00, 500, 1, NULL, 60, NULL, NULL, FALSE, 'Valid for customers 60 and older', 'marketing_team'),
('Student Special', '15% off for students', 'STUDENT15', 'DISCOUNT', 'ACTIVE', '2024-01-01', '2024-12-31', NULL, NULL, 30.00, 0, 1, NULL, NULL, NULL, NULL, TRUE, 'Valid student ID required', 'marketing_team'),
('Gold Member Bonus', 'Extra 500 points for Gold members', 'GOLDBONUS', 'BONUS_POINTS', 'ACTIVE', '2024-01-01', '2024-12-31', NULL, 500, 100.00, 0, 1, 'GOLD', NULL, NULL, NULL, FALSE, 'Excludes sale items', 'admin'),
('Weekend Flash Sale', 'Flash sale every weekend', 'WEEKEND', 'FLASH_SALE', 'ACTIVE', '2024-01-01', '2024-12-31', 3.00, NULL, 75.00, 5000, 2, NULL, NULL, NULL, NULL, FALSE, 'Saturday and Sunday only', 'marketing_team'),
('Women Day Special', 'Special offers for women', 'WOMEN2024', 'DISCOUNT', 'ACTIVE', '2024-03-01', '2024-03-15', NULL, NULL, NULL, 300, 1, NULL, NULL, NULL, 'F', FALSE, 'Valid on womens section', 'marketing_team'),
('Loyalty Boost', 'Earn 50% more points', 'LOYALTYBOOST', 'DOUBLE_POINTS', 'ACTIVE', '2024-01-01', '2024-12-31', 1.50, NULL, NULL, 0, 1, NULL, NULL, NULL, NULL, FALSE, 'Standard exclusions apply', 'admin');

-- Insert Demo Transactions
INSERT INTO transactions (transaction_code, customer_id, amount, discount_applied, net_amount, transaction_date, transaction_type, status, store_code, store_name, cashier_code, product_category, payment_method) VALUES
('TXN001', 1, 150.00, 15.00, 135.00, '2024-01-15 10:30:00', 'PURCHASE', 'COMPLETED', 'STORE001', 'Main Street Store', 'CASH001', 'Electronics', 'Credit Card'),
('TXN002', 2, 250.00, 0.00, 250.00, '2024-01-16 14:20:00', 'PURCHASE', 'COMPLETED', 'STORE002', 'Mall Location', 'CASH002', 'Clothing', 'Debit Card'),
('TXN003', 1, 75.50, 7.55, 67.95, '2024-01-17 09:15:00', 'PURCHASE', 'COMPLETED', 'STORE001', 'Main Street Store', 'CASH003', 'Groceries', 'Cash'),
('TXN004', 3, 320.00, 0.00, 320.00, '2024-01-18 16:45:00', 'PURCHASE', 'COMPLETED', 'STORE003', 'Downtown Store', 'CASH001', 'Electronics', 'Credit Card'),
('TXN005', 5, 500.00, 50.00, 450.00, '2024-01-19 11:00:00', 'PURCHASE', 'COMPLETED', 'STORE002', 'Mall Location', 'CASH002', 'Jewelry', 'Credit Card'),
('TXN006', 2, 89.99, 8.99, 81.00, '2024-01-20 13:30:00', 'PURCHASE', 'COMPLETED', 'STORE001', 'Main Street Store', 'CASH003', 'Beauty', 'Debit Card'),
('TXN007', 7, 199.00, 19.90, 179.10, '2024-01-21 15:00:00', 'PURCHASE', 'COMPLETED', 'STORE003', 'Downtown Store', 'CASH001', 'Clothing', 'Credit Card'),
('TXN008', 1, 45.00, 0.00, 45.00, '2024-01-22 10:00:00', 'PURCHASE', 'COMPLETED', 'STORE001', 'Main Street Store', 'CASH002', 'Books', 'Cash');

-- Insert Demo Loyalty Points
INSERT INTO loyalty_points (customer_id, points_earned, points_redeemed, current_balance, lifetime_points, status, last_earned_date) VALUES
(1, 4500, 500, 4000, 4500, 'ACTIVE', '2024-01-22 10:00:00'),
(2, 8000, 1000, 7000, 8000, 'ACTIVE', '2024-01-20 13:30:00'),
(3, 3200, 0, 3200, 3200, 'ACTIVE', '2024-01-18 16:45:00'),
(4, 500, 0, 500, 500, 'ACTIVE', '2023-09-01 09:00:00'),
(5, 15000, 5000, 10000, 15000, 'ACTIVE', '2024-01-19 11:00:00'),
(6, 200, 0, 200, 200, 'ACTIVE', '2023-05-20 14:00:00'),
(7, 5500, 1000, 4500, 5500, 'ACTIVE', '2024-01-21 15:00:00'),
(8, 1500, 500, 1000, 1500, 'ACTIVE', '2023-07-01 10:00:00');

-- Insert Demo Redemption Logs
INSERT INTO redemption_logs (redemption_code, customer_id, reward_id, points_redeemed, status, channel, redemption_date, store_code, store_name) VALUES
('RDM001', 1, 1, 500, 'USED', 'IN_STORE', '2024-01-10 14:00:00', 'STORE001', 'Main Street Store'),
('RDM002', 2, 2, 2500, 'COMPLETED', 'ONLINE', '2024-01-12 10:00:00', NULL, NULL),
('RDM003', 5, 6, 10000, 'COMPLETED', 'IN_STORE', '2024-01-14 16:00:00', 'STORE002', 'Mall Location'),
('RDM004', 1, 7, 400, 'COMPLETED', 'MOBILE_APP', '2024-01-18 11:00:00', 'STORE001', 'Main Street Store');

-- Update customer rewards junction table
INSERT INTO customer_rewards (customer_id, reward_id) VALUES
(1, 1),
(2, 2),
(5, 6),
(1, 7);

-- Update promotion customers junction table
INSERT INTO promotion_customers (promotion_id, customer_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 5),
(2, 1),
(2, 2),
(4, 4),
(4, 8);

