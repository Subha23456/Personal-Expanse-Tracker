-- OPTIONAL sample data. Run manually:  mysql -u root -p expense_tracker < db/seed.sql
-- Safe by design: it inserts rows ONLY if the transactions table is completely empty,
-- so it can never overwrite or mix into data you entered yourself.
-- Dates are relative to today so the demo always looks recent.
INSERT INTO transactions (type, title, amount, category, transaction_date, notes)
SELECT * FROM (
  SELECT 'INCOME'  AS type, 'Monthly stipend' AS title, 1800.00 AS amount, 'Salary' AS category, DATE_SUB(CURDATE(), INTERVAL 28 DAY) AS transaction_date, 'Internship stipend' AS notes
  UNION ALL SELECT 'INCOME',  'Freelance logo design', 250.00, 'Freelance', DATE_SUB(CURDATE(), INTERVAL 21 DAY), NULL
  UNION ALL SELECT 'INCOME',  'Sold old textbooks',     75.00, 'Other',     DATE_SUB(CURDATE(), INTERVAL 9 DAY),  NULL
  UNION ALL SELECT 'INCOME',  'Monthly stipend',      1800.00, 'Salary',    DATE_SUB(CURDATE(), INTERVAL 1 DAY),  NULL
  UNION ALL SELECT 'EXPENSE', 'Room rent',             650.00, 'Rent',      DATE_SUB(CURDATE(), INTERVAL 27 DAY), 'Shared apartment'
  UNION ALL SELECT 'EXPENSE', 'Groceries',              82.40, 'Food',      DATE_SUB(CURDATE(), INTERVAL 26 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Metro monthly pass',     45.00, 'Transport', DATE_SUB(CURDATE(), INTERVAL 25 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Electricity bill',       38.75, 'Utilities', DATE_SUB(CURDATE(), INTERVAL 23 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Lunch with team',        14.20, 'Food',      DATE_SUB(CURDATE(), INTERVAL 20 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Streaming subscription', 11.99, 'Entertainment', DATE_SUB(CURDATE(), INTERVAL 18 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Online course',          29.00, 'Education', DATE_SUB(CURDATE(), INTERVAL 16 DAY), 'Spring Boot course'
  UNION ALL SELECT 'EXPENSE', 'Groceries',              67.15, 'Food',      DATE_SUB(CURDATE(), INTERVAL 14 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Pharmacy',               18.60, 'Health',    DATE_SUB(CURDATE(), INTERVAL 12 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'New headphones',         59.90, 'Shopping',  DATE_SUB(CURDATE(), INTERVAL 10 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Coffee beans',           16.50, 'Food',      DATE_SUB(CURDATE(), INTERVAL 7 DAY),  NULL
  UNION ALL SELECT 'EXPENSE', 'Cinema tickets',         22.00, 'Entertainment', DATE_SUB(CURDATE(), INTERVAL 5 DAY), NULL
  UNION ALL SELECT 'EXPENSE', 'Room rent',             650.00, 'Rent',      DATE_SUB(CURDATE(), INTERVAL 2 DAY),  NULL
  UNION ALL SELECT 'EXPENSE', 'Groceries',              74.30, 'Food',      DATE_SUB(CURDATE(), INTERVAL 1 DAY),  NULL
) AS sample
WHERE NOT EXISTS (SELECT 1 FROM transactions);
