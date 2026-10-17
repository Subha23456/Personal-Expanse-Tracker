-- Run once:  mysql -u root -p < db/schema.sql
CREATE DATABASE IF NOT EXISTS expense_tracker
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE expense_tracker;

CREATE TABLE IF NOT EXISTS transactions (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  type             VARCHAR(10)   NOT NULL,
  title            VARCHAR(100)  NOT NULL,
  amount           DECIMAL(12,2) NOT NULL,
  category         VARCHAR(50)   NOT NULL,
  transaction_date DATE          NOT NULL,
  notes            VARCHAR(500)  NULL,
  CONSTRAINT chk_type   CHECK (type IN ('INCOME', 'EXPENSE')),
  CONSTRAINT chk_amount CHECK (amount > 0),
  INDEX idx_tx_date (transaction_date),
  INDEX idx_tx_category (category)
);
