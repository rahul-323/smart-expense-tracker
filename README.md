# 💰 Smart Expense Tracker API

A comprehensive **RESTful backend** for personal finance management — track expenses and income, set category budgets with real-time alerts, view analytics dashboards, and automate recurring transactions. Built with **Java 21** and **Spring Boot 3.4.5**, secured with **JWT authentication**.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen)
![Security](https://img.shields.io/badge/Security-JWT-blue)
![Database](https://img.shields.io/badge/Database-MySQL-blue)
![Tests](https://img.shields.io/badge/Tests-JUnit%205%20%2B%20Mockito-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Documentation](#-api-documentation)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Author](#-author)

---

## ✨ Features

### 🔐 Authentication & Security
- User registration and login with **JWT** token-based authentication
- **BCrypt** password hashing
- Role-based access control (USER / ADMIN)
- Global exception handling with consistent error responses
- Request validation with Jakarta Bean Validation

### 💸 Expense Management
- Full CRUD operations for expenses
- **Dynamic multi-criteria filtering** (category, date range, amount range, payment method, status, tags)
- **Pagination and sorting** on all list endpoints
- Keyword search across descriptions
- Tag support (many-to-many, auto-created)

### 💰 Income Tracking
- Full CRUD for income entries
- Filter by date range and source
- Recurring income flag

### 📂 Category Management
- 14 pre-seeded default categories
- User-created custom categories
- Soft-delete support (preserves referential integrity)
- Ownership protection (users manage only their own categories)

### 📊 Budget Management
- Set monthly spending limits per category
- **Real-time budget status calculation** (ON_TRACK / WARNING / EXCEEDED)
- Configurable alert thresholds
- Database-level duplicate prevention (unique constraints)

### 📈 Analytics Dashboard
- Monthly financial summary (income vs expense vs savings)
- Category-wise expense breakdown with percentages
- 6-month income/expense trend
- Daily expense breakdown
- Top 5 highest expenses

### 🔄 Recurring Expenses & Scheduling
- Automated recurring expenses (DAILY / WEEKLY / MONTHLY / YEARLY)
- **Scheduled background jobs** (`@Scheduled` cron) that auto-generate expenses
- Auto-expiry of ended recurring expenses
- Daily budget alert scheduler
- Pause / resume support

### 📤 Data Export
- Export all expenses or a specific month as **CSV**
- Proper file-download HTTP headers

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.4.5 |
| **Security** | Spring Security + JWT (JJWT 0.11.5) |
| **Persistence** | Spring Data JPA / Hibernate |
| **Database** | MySQL 8 (H2 for tests) |
| **Validation** | Jakarta Bean Validation |
| **Boilerplate** | Lombok |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Monitoring** | Spring Boot Actuator |
| **Export** | OpenCSV |
| **Testing** | JUnit 5, Mockito, AssertJ, MockMvc |
| **Build Tool** | Maven |

---

## 🏗 Architecture

The application follows a clean **3-tier layered architecture** with clear separation of concerns:

```
┌──────────────────────────────────────────────┐
│  PRESENTATION LAYER (Controllers)             │
│  Handle HTTP requests, validation, responses  │
└──────────────────────┬───────────────────────┘
                       ↓
┌──────────────────────────────────────────────┐
│  BUSINESS LOGIC LAYER (Services)              │
│  Business rules, orchestration, transactions  │
│  (Interface + Implementation pattern)         │
└──────────────────────┬───────────────────────┘
                       ↓
┌──────────────────────────────────────────────┐
│  DATA ACCESS LAYER (Repositories)             │
│  Spring Data JPA + custom JPQL + Specifications│
└──────────────────────┬───────────────────────┘
                       ↓
┌──────────────────────────────────────────────┐
│  DATABASE (MySQL)                             │
└──────────────────────────────────────────────┘
```

**Key design patterns:**
- **DTO Pattern** — API contracts decoupled from database entities
- **Mapper Pattern** — clean entity ↔ DTO conversion
- **Service Interface + Impl** — loose coupling, easy mocking
- **JPA Specifications** — dynamic runtime query building
- **Soft Delete** — data integrity preservation
- **Ownership Scoping** — every query filtered by authenticated user (from JWT)

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- MySQL 8+
- Maven 3.8+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/rahul-323/smart-expense-tracker.git
   cd smart-expense-tracker
   ```

2. **Create the MySQL database**
   ```sql
   CREATE DATABASE smart_expense_tracker;
   ```

3. **Set up environment variables** — copy `.env.example` to `.env` and fill in your values
   ```bash
   cp .env.example .env
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The API starts at **`http://localhost:8080`**

---

## ⚙️ Configuration

Environment variables (defined in `.env`):

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `your_password` |
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `3306` |
| `DB_NAME` | Database name | `smart_expense_tracker` |
| `JWT_SECRET` | Secret key for signing JWT (min 256 bits) | `your_secret_key` |

> ⚠️ **Never commit your `.env` file.** It is already listed in `.gitignore`.

---

## 📖 API Documentation

Interactive **Swagger UI** is available once the app is running:

```
http://localhost:8080/swagger-ui.html
```

Click **Authorize**, paste your JWT token (from `/api/auth/login`), and test every endpoint directly from the browser.

A complete **Postman collection** (`Smart-Expense-Tracker-Postman-Collection.json`) is included in the repo — it auto-saves the JWT token after login and covers all 79 requests.

---

## 📡 API Endpoints

Base URL: `http://localhost:8080/api`

### 🔐 Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/register` | Register a new user | ❌ |
| POST | `/auth/login` | Login, returns JWT | ❌ |
| POST | `/auth/change-password` | Change password | ✅ |

### 📂 Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/categories` | All categories (default + custom) |
| GET | `/categories/default` | Default categories only |
| GET | `/categories/custom` | User's custom categories |
| POST | `/categories` | Create custom category |
| PUT | `/categories/{id}` | Update custom category |
| DELETE | `/categories/{id}` | Soft-delete custom category |

### 💸 Expenses
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/expenses` | Create expense |
| GET | `/expenses` | List (paginated) |
| GET | `/expenses/{id}` | Get by ID |
| PUT | `/expenses/{id}` | Update expense |
| DELETE | `/expenses/{id}` | Delete expense |
| GET | `/expenses/filter` | Multi-criteria filter |
| GET | `/expenses/search` | Keyword search |

### 💰 Incomes
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/incomes` | Add income |
| GET | `/incomes` | List (paginated) |
| GET | `/incomes/{id}` | Get by ID |
| PUT | `/incomes/{id}` | Update income |
| DELETE | `/incomes/{id}` | Delete income |
| GET | `/incomes/filter` | Filter by date/source |

### 📊 Budgets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/budgets` | Set budget |
| GET | `/budgets` | Current month budgets |
| GET | `/budgets/{id}` | Get by ID |
| GET | `/budgets/month/{year}/{month}` | Budgets for a month |
| GET | `/budgets/status` | ⭐ Real-time budget status |
| PUT | `/budgets/{id}` | Update budget |
| DELETE | `/budgets/{id}` | Delete budget |

### 📈 Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/summary` | Monthly financial summary |
| GET | `/dashboard/summary/{year}/{month}` | Summary for a month |
| GET | `/dashboard/category-breakdown` | Category-wise breakdown |
| GET | `/dashboard/monthly-trend` | Last 6 months trend |
| GET | `/dashboard/daily-expenses/{year}/{month}` | Daily expenses |
| GET | `/dashboard/top-expenses` | Top 5 expenses |

### 🔄 Recurring Expenses
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/recurring-expenses` | Create recurring expense |
| GET | `/recurring-expenses` | List all |
| GET | `/recurring-expenses/{id}` | Get by ID |
| PUT | `/recurring-expenses/{id}` | Update |
| PATCH | `/recurring-expenses/{id}/toggle` | Pause / resume |
| DELETE | `/recurring-expenses/{id}` | Delete |

### 📤 Export
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/export/csv` | Export all expenses as CSV |
| GET | `/export/csv?month=6&year=2026` | Export a month as CSV |

---

## 🗄 Database Schema

**Core entities and relationships:**

- **User** `1───N` Expense, Income, Budget, RecurringExpense, custom Category
- **Category** `1───N` Expense, Budget, RecurringExpense
- **Expense** `N───M` Tag (via `expense_tags` junction table)

Money is stored as `BigDecimal(12,2)` for precision. Indexes are placed on `user_id` and date columns for query performance. Budgets enforce a unique constraint on `(user, category, month, year)`.

---

## 🧪 Testing

The project includes comprehensive tests following the **testing pyramid**:

- **Service unit tests** — Mockito-based, business logic in isolation
- **Repository tests** — `@DataJpaTest` with H2 in-memory database
- **Controller tests** — MockMvc for HTTP layer

Run tests:
```bash
mvn test
```

Generate coverage report (JaCoCo):
```bash
mvn test
# Open target/site/jacoco/index.html
```

Boundary conditions (budget thresholds, empty datasets, duplicate prevention, ownership violations) are explicitly covered.

---

## 📂 Project Structure

```
src/main/java/com/rahul/expensetracker/
├── config/          # Swagger, Scheduler configuration
├── controller/      # REST controllers
├── dto/
│   ├── request/     # Request DTOs
│   └── response/    # Response DTOs
├── entity/          # JPA entities
├── enums/           # Enum types
├── exception/       # Custom exceptions + global handler
├── mapper/          # Entity ↔ DTO mappers
├── repository/      # Spring Data JPA repositories
├── scheduler/       # Scheduled background jobs
├── security/        # JWT filter, security config
├── service/         # Service interfaces
│   └── impl/        # Service implementations
└── util/            # CSV export utility
```

---

## 👨‍💻 Author

**Rahul Chauhan**
- GitHub: [@rahul-323](https://github.com/rahul-323)

---

## 📄 License

This project is licensed under the MIT License.

---

⭐ If you found this project useful, please consider giving it a star!
