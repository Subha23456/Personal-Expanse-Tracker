# Manual API test guide

Start the app first (`mvn spring-boot:run`). Examples use `curl` on Mac/Linux (Git Bash on Windows). In Postman, use the same URLs, methods and JSON bodies.

```bash
BASE=http://localhost:8080/api/transactions
```

## 1. Create (expect 201 and a `Location` header)
```bash
curl -i -X POST $BASE -H "Content-Type: application/json" -d '{
  "type":"EXPENSE","title":"Groceries","amount":42.50,
  "category":"Food","transactionDate":"2025-03-10","notes":"Weekly shop"}'

curl -i -X POST $BASE -H "Content-Type: application/json" -d '{
  "type":"INCOME","title":"Salary","amount":1500,
  "category":"Salary","transactionDate":"2025-03-01"}'
```

## 2. List and get one (200)
```bash
curl -s $BASE
curl -s $BASE/1
```

## 3. Update (200)
```bash
curl -i -X PUT $BASE/1 -H "Content-Type: application/json" -d '{
  "type":"EXPENSE","title":"Groceries (updated)","amount":45.00,
  "category":"Food","transactionDate":"2025-03-10"}'
```

## 4. Filters
```bash
curl -s "$BASE?type=EXPENSE"
curl -s "$BASE?category=food"
curl -s "$BASE?startDate=2025-03-01&endDate=2025-03-31"
curl -s "$BASE?keyword=grocer"
curl -s "$BASE?type=EXPENSE&category=Food&startDate=2025-03-01&keyword=shop"
```

## 5. Summary (all-time vs filtered)
```bash
curl -s $BASE/summary
curl -s "$BASE/summary?startDate=2025-03-05&endDate=2025-03-31"
curl -s $BASE/categories
```
Check by hand: `balance = totalIncome - totalExpenses`; category totals add up to `totalExpenses`.

## 6. Delete (204), then confirm it is gone (404)
```bash
curl -i -X DELETE $BASE/1
curl -i $BASE/1
```

## 7. Error cases (each should return the JSON error format)

| Test | Command | Expected |
|---|---|---|
| Negative amount | `curl -i -X POST $BASE -H "Content-Type: application/json" -d '{"type":"EXPENSE","title":"x","amount":-5,"category":"Food","transactionDate":"2025-03-10"}'` | 400, `fieldErrors.amount` |
| Zero amount | same with `"amount":0` | 400 |
| Three decimals | same with `"amount":1.239` | 400 |
| Empty body | `curl -i -X POST $BASE -H "Content-Type: application/json" -d '{}'` | 400, 5 field errors |
| Invalid date | `"transactionDate":"2025-13-45"` | 400 |
| Unknown type | `"type":"GIFT"` | 400 |
| Missing record | `curl -i $BASE/999999` | 404 |
| Update missing | `curl -i -X PUT $BASE/999999 -H "Content-Type: application/json" -d '{"type":"EXPENSE","title":"x","amount":1,"category":"Food","transactionDate":"2025-03-10"}'` | 404 |
| Delete missing | `curl -i -X DELETE $BASE/999999` | 404 |
| Bad filter date | `curl -i "$BASE?startDate=abc"` | 400 |
| Reversed range | `curl -i "$BASE?startDate=2025-04-01&endDate=2025-03-01"` | 400 |
| Wrong method | `curl -i -X PATCH $BASE/1` | 405 |
