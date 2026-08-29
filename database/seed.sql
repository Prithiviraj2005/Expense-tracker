-- Create database
CREATE DATABASE IF NOT EXISTS expense_tracker;
USE expense_tracker;

-- Default Expense Categories
INSERT INTO categories (name, type, created_at) VALUES
('Food', 'EXPENSE', NOW()),
('Transport', 'EXPENSE', NOW()),
('Shopping', 'EXPENSE', NOW()),
('Education', 'EXPENSE', NOW()),
('Medical', 'EXPENSE', NOW()),
('Entertainment', 'EXPENSE', NOW()),
('Bills', 'EXPENSE', NOW()),
('Others', 'EXPENSE', NOW());

-- Default Income Categories
INSERT INTO categories (name, type, created_at) VALUES
('Salary', 'INCOME', NOW()),
('Business', 'INCOME', NOW()),
('Freelance', 'INCOME', NOW()),
('Investment', 'INCOME', NOW()),
('Other', 'INCOME', NOW());

-- Demo User (password: demo123 - BCrypt hashed)
-- BCrypt hash of 'demo123': $2a$10$EqKcp1WFKAr1sDhSaHe5aOSfOJGwRRiYpFSbMsHCY6gqf/VdBqiHG
INSERT INTO users (name, email, password, created_at) VALUES
('Demo User', 'demo@example.com', '$2a$10$EqKcp1WFKAr1sDhSaHe5aOSfOJGwRRiYpFSbMsHCY6gqf/VdBqiHG', NOW());

-- Demo Transactions (for the demo user, assuming user_id = 1)
INSERT INTO transactions (amount, type, description, transaction_date, payment_method, created_at, user_id, category_id) VALUES
(75000.00, 'INCOME', 'Monthly Salary', CURDATE(), 'BANK_TRANSFER', NOW(), 1, 9),
(5000.00, 'INCOME', 'Freelance Project', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'UPI', NOW(), 1, 11),
(2500.00, 'EXPENSE', 'Grocery Shopping', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'UPI', NOW(), 1, 1),
(1500.00, 'EXPENSE', 'Uber Rides', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CASH', NOW(), 1, 2),
(3000.00, 'EXPENSE', 'Online Shopping', DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'CREDIT_CARD', NOW(), 1, 3),
(800.00, 'EXPENSE', 'Movie Night', DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'DEBIT_CARD', NOW(), 1, 6),
(5000.00, 'EXPENSE', 'Electricity Bill', DATE_SUB(CURDATE(), INTERVAL 7 DAY), 'BANK_TRANSFER', NOW(), 1, 7),
(15000.00, 'INCOME', 'Freelance Web Dev', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'BANK_TRANSFER', NOW(), 1, 11),
(1200.00, 'EXPENSE', 'Books', DATE_SUB(CURDATE(), INTERVAL 8 DAY), 'UPI', NOW(), 1, 4),
(2000.00, 'EXPENSE', 'Doctor Visit', DATE_SUB(CURDATE(), INTERVAL 12 DAY), 'CASH', NOW(), 1, 5);
