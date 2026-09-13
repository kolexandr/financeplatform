# Finance Tracker API

A personal finance tracking REST API built with Java and Spring Boot. The application lets authenticated users manage their own categories, transactions, category-specific monthly budgets, and monthly financial summaries.

## Technology

- Java 25
- Spring Boot, Spring Web MVC, Spring Data JPA, Spring Security, and Validation
- PostgreSQL 17 in Docker Compose
- Maven
- JWT authentication with BCrypt password hashing

## Current Features

- User registration and login
- JWT-protected user data
- User-owned category CRUD
- Income and expense transaction CRUD
- Transaction filtering by category, type, and date range
- Category-specific monthly budget CRUD
- Monthly analytics: income, expenses, balance, allocated budget, remaining budget, and expenses by category
- Structured error responses for validation, duplicate resources, missing resources, and invalid date ranges

## Prerequisites

- A full JDK 25 installation, including `javac`
- Docker and Docker Compose
- Maven Wrapper permissions: `chmod +x mvnw` on Linux/macOS if needed

## Configuration

The database settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/finance_tracker
spring.datasource.username=finance_user
spring.datasource.password=finance_password
```

The API signs JWTs using the `JWT_SECRET` environment variable. Set a unique secret of at least 32 characters before starting the application:

```bash
export JWT_SECRET='replace-with-a-long-random-development-secret'
```

Do not commit a real JWT secret. Use `.env.example` only as a local setup reference and keep real secrets in your shell environment or an ignored `.env` file.

## Run Locally

1. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

2. Set `JWT_SECRET` as shown above.

3. Start Spring Boot:

   ```bash
   ./mvnw spring-boot:run
   ```

The API is available at `http://localhost:8080`. PostgreSQL is available to the application at `localhost:5433`.

To stop the database while keeping its Docker volume:

```bash
docker compose down
```

## Authentication

Register an account:

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "alex@example.com",
  "password": "password123"
}
```

Log in with the same request shape:

```http
POST /api/auth/login
```

Both endpoints return an email and JWT token:

```json
{
  "email": "alex@example.com",
  "token": "eyJ..."
}
```

For every endpoint except `/api/auth/register` and `/api/auth/login`, send the token in the request header:

```http
Authorization: Bearer <token>
```

## API Reference

### Categories

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/categories` | Create a category |
| `GET` | `/api/categories` | List the current user's categories |
| `GET` | `/api/categories/{id}` | Get one category |
| `PUT` | `/api/categories/{id}` | Rename a category |
| `DELETE` | `/api/categories/{id}` | Delete an unused category |

Category request:

```json
{
  "name": "Food"
}
```

Each user can have only one category with a particular name. A category linked to a transaction or budget cannot be deleted.

### Transactions

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/transactions` | Create a transaction |
| `GET` | `/api/transactions` | List transactions, optionally filtered |
| `GET` | `/api/transactions/{id}` | Get one transaction |
| `PUT` | `/api/transactions/{id}` | Replace a transaction |
| `DELETE` | `/api/transactions/{id}` | Delete a transaction |

Transaction request:

```json
{
  "categoryId": 1,
  "provider": "Visa",
  "amount": 24.50,
  "type": "EXPENSE",
  "transactionDate": "2026-09-13",
  "description": "Lunch"
}
```

`amount` must be positive. Valid transaction types are `INCOME` and `EXPENSE`. `description` is optional.

Combine the available filters as needed:

```http
GET /api/transactions?categoryId=1&type=EXPENSE&from=2026-09-01&to=2026-09-30
```

### Budgets

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/budgets` | Create a category budget for a month |
| `GET` | `/api/budgets` | List all budgets for the current user |
| `GET` | `/api/budgets/current` | List budgets for the current month |
| `GET` | `/api/budgets/current?month=2026-09` | List budgets for a specific month |
| `GET` | `/api/budgets/{id}` | Get one budget |
| `PUT` | `/api/budgets/{id}` | Update a budget |
| `DELETE` | `/api/budgets/{id}` | Delete a budget |

Budget request:

```json
{
  "categoryId": 1,
  "amount": 400.00,
  "month": "2026-09"
}
```

Only one budget is allowed for each category and month per user.

### Analytics

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/analytics/summary` | Current-month summary |
| `GET` | `/api/analytics/summary?month=2026-09` | Summary for a specific month |

The response includes total income, total expenses, current balance, total category budget, remaining budget, and expenses grouped by category.

## Testing

Run the test suite with:

```bash
./mvnw test
```

The project currently includes focused authentication service tests. Future work should add service tests for category ownership, transaction filtering, budget limits, and analytics calculations, followed by integration tests with PostgreSQL/Testcontainers.

## Planned Improvements

- Database migrations with Flyway
- Pagination and sorting for transactions
- OpenAPI/Swagger documentation
- Expanded automated test coverage and Testcontainers
- React frontend
