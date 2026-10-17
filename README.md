# Ledger: Personal Expense Tracker

A small full-stack web app for recording income and expenses, filtering them, and seeing totals and spending by category. Built as a student portfolio project with Spring Boot, MySQL and plain JavaScript.

> **Demo only.** There is no login, no user accounts and no encryption of stored data. Do **not** enter real, sensitive financial information. It is not production-grade security.

## Features

- Create, view, edit and delete transactions (income or expense)
- Fields: type, title, amount, category, date, optional notes
- Filter by keyword, type, category and date range
- Summary cards: total income, total expenses and balance
- Filtered totals are shown separately from all-time totals, so you can always tell which is which
- Spending-by-category breakdown that follows the filters
- Loading, empty and error states; inline form validation; delete confirmation
- Responsive layout (sidebar on desktop, top bar on mobile)
- Server-side validation and consistent JSON error responses
- Automated backend tests (service and controller) using in-memory H2

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17+, Spring Boot 3.3, Spring Web, Spring Data JPA, Bean Validation |
| Database | MySQL 8 |
| Frontend | HTML5, CSS3, vanilla JavaScript (Fetch API) |
| Build | Maven |
| Tests | JUnit 5, Spring Boot Test, MockMvc, H2 (tests only) |

## Architecture

```
Browser (HTML/CSS/JS)  --fetch-->  Controller  -->  Service  -->  Repository  -->  MySQL
                                   (HTTP, status)   (logic, totals) (Spring Data JPA)
                                        |
                         DTOs (API shape)   GlobalExceptionHandler (JSON errors)
```

- **Controller** (`controller/`): receives requests, triggers validation, returns status codes.
- **Service** (`service/`): business rules, filter clean-up, income/expense/balance and category totals.
- **Repository** (`repository/`): Spring Data JPA. Dynamic filters use the JPA Criteria API (`TransactionSpecifications`), which produces parameterized queries.
- **DTOs** (`dto/`): separate request/response objects from the database entity.
- **Exception handler** (`exception/`): every error becomes `{timestamp, status, error, message, fieldErrors?}`.
- **Frontend** lives in `src/main/resources/static/` and is served by Spring Boot itself, so there is one server and no CORS setup.

### Folder structure

```
expense-tracker/
├── pom.xml
├── .env.example
├── db/schema.sql, db/seed.sql
├── docs/API_TESTING.md, docs/FRONTEND_TEST_CASES.md, docs/screenshots/
└── src/
    ├── main/java/com/portfolio/expensetracker/
    │   ├── ExpenseTrackerApplication.java
    │   ├── controller/  service/  repository/  model/  dto/  exception/
    ├── main/resources/application.properties
    ├── main/resources/static/  (index.html, css/styles.css, js/app.js)
    └── test/  (service + controller tests, H2 config)
```

### Design decisions worth explaining in an interview

- **`BigDecimal` + `DECIMAL(12,2)` for money**, never `double`, to avoid rounding errors.
- **Totals are calculated on the server**, from the real rows, and filtered totals use exactly the same filter code as the list so the two can never disagree.
- **Schema owned by SQL, not Hibernate** (`ddl-auto=validate`), so the app never silently alters your database.
- **DTOs** so database changes don't leak into the API.
- **XSS protection:** the frontend writes user text with `textContent`, never `innerHTML`.
- **Summaries are computed in Java** after loading the matching rows. This is simple and fine for a personal app; for very large data sets you would move it into SQL `GROUP BY` queries (see Known limitations).

## Prerequisites

- JDK 17 or newer (`java -version`)
- Maven 3.9+ (`mvn -version`)
- MySQL 8 running locally (`mysql --version`)
- Git

## Database setup

```bash
# 1. Create the database and table
mysql -u root -p < db/schema.sql

# 2. Create a dedicated user (replace the password!)
mysql -u root -p
```
```sql
CREATE USER 'expense_app'@'localhost' IDENTIFIED BY 'choose-a-strong-password';
GRANT ALL PRIVILEGES ON expense_tracker.* TO 'expense_app'@'localhost';
FLUSH PRIVILEGES;
```

Optional sample data (only inserts when the table is empty, so it never touches your own data):
```bash
mysql -u root -p expense_tracker < db/seed.sql
```

## Environment variables

| Variable | Default | Purpose |
|---|---|---|
| `DB_PASSWORD` | none (required) | Password of the MySQL user |
| `DB_USER` | `expense_app` | MySQL user |
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `expense_tracker` | Database name |
| `PORT` | `8080` | HTTP port of the app |

Mac/Linux: `export DB_PASSWORD='your-password'`
Windows PowerShell: `$env:DB_PASSWORD='your-password'`

`.env.example` lists the same variables. Never commit a real `.env` (it is in `.gitignore`).

## Run

```bash
mvn spring-boot:run
```
Open <http://localhost:8080>. The frontend and the API are served from the same address.

## Run the tests

```bash
mvn test
```
Tests use in-memory H2, so MySQL and `DB_PASSWORD` are not needed.

## API documentation

Base path: `/api/transactions`

| Method | Path | Description | Success |
|---|---|---|---|
| GET | `/api/transactions` | List (newest first), optional filters | 200 |
| GET | `/api/transactions/{id}` | One transaction | 200 / 404 |
| POST | `/api/transactions` | Create | 201 + `Location` |
| PUT | `/api/transactions/{id}` | Replace/update | 200 / 404 |
| DELETE | `/api/transactions/{id}` | Delete | 204 / 404 |
| GET | `/api/transactions/summary` | Totals + expenses by category, same filters | 200 |
| GET | `/api/transactions/categories` | Distinct category names | 200 |

**Filters** (query params, all optional): `type` (`INCOME`/`EXPENSE`), `category` (case-insensitive), `startDate`, `endDate` (`YYYY-MM-DD`, inclusive), `keyword` (matches title or notes).

**Request body (POST/PUT):**
```json
{
  "type": "EXPENSE",
  "title": "Groceries",
  "amount": 42.50,
  "category": "Food",
  "transactionDate": "2025-03-10",
  "notes": "Weekly shop"
}
```

**Validation rules:** type required; title required, max 100; amount required, at least 0.01, at most 2 decimals; category required, max 50; date required, valid `YYYY-MM-DD`; notes max 500.

**Error format:**
```json
{
  "timestamp": "2025-03-10T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed. Check the highlighted fields.",
  "fieldErrors": { "amount": "Amount must be at least 0.01" }
}
```

Status codes: `400` validation/bad input, `404` missing record, `405` wrong method, `500` unexpected error.

Manual test commands: [`docs/API_TESTING.md`](docs/API_TESTING.md). Browser test cases: [`docs/FRONTEND_TEST_CASES.md`](docs/FRONTEND_TEST_CASES.md).

## Screenshots

Add your own after running the app (save into `docs/screenshots/`):

| Dashboard | Add/edit form | Filters |
|---|---|---|
| `![Dashboard](docs/screenshots/dashboard.png)` | `![Form](docs/screenshots/form.png)` | `![Filters](docs/screenshots/filters.png)` |

## Known limitations

- No authentication, authorization or multi-user support
- Not for real financial data; no encryption at rest, no audit log
- Single currency (change `CURRENCY` in `js/app.js`)
- Lists and summaries load all matching rows (no pagination); fine for personal use, not for huge data sets
- Category grouping ignores case, but spelling variants ("Food" vs "Foods") are separate categories
- No CSV export, recurring transactions or charts beyond the category bars
- Deleting is permanent (no undo)

## Deployment

1. **Build a jar:** `mvn clean package` creates `target/expense-tracker-0.0.1-SNAPSHOT.jar` (this runs the tests; add `-DskipTests` to skip).
2. **Provision MySQL** on your host (managed MySQL from your cloud provider) and run `db/schema.sql` there.
3. **Set environment variables** (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, and `PORT` if the platform assigns one) in the platform's secret settings, not in code.
4. **Run:** `java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar`
5. Platforms such as Render, Railway or a small VPS work. Because there is no login, **do not expose a deployed copy publicly with real data**; use demo data only.
6. If your provider requires TLS for MySQL, change `useSSL=false` in `application.properties` accordingly.

## Author notes

Built to demonstrate layered architecture, REST design, validation, JPA, testing and clean frontend code.
