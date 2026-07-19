# Smart Expense Tracker

Smart Expense Tracker is a Spring Boot backend for a personal finance application focused on user authentication, category management, and expense tracking with tag-based organization.

## Overview

This project exposes a secure REST API for:
- user registration and login
- JWT-based authentication and authorization
- category management
- expense CRUD operations
- expense filtering, pagination, and search
- tag creation and association through the expense flow
- persistence with MySQL using JPA/Hibernate

## Current implementation status

The backend already includes:
- Spring Security configuration with JWT support
- user registration and login endpoints
- protected resource access for authenticated users
- category endpoints for default, custom, and user-specific categories
- full expense CRUD support with pagination and filtering
- expense tag support via a many-to-many relationship with automatic tag creation
- DTOs, mappers, repositories, services, and centralized exception handling

The following areas are still under development or planned for later:
- income tracking
- budget management
- recurring expenses
- analytics and reports

## Tech stack

- Java 21
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- JWT (jjwt)
- Lombok
- Maven

## Prerequisites

Before running the project, make sure you have:
- JDK 21 or newer
- Maven
- a running MySQL server

## Configuration

The application uses environment-based configuration. Set the following values before running it:
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET

The project is configured to initialize SQL data on startup for MySQL using:
- `spring.jpa.defer-datasource-initialization=true`
- `spring.sql.init.mode=always`

## Running locally

1. Create a MySQL database named `smart_expense_tracker`
2. Set the required environment variables
3. Run:

```bash
./mvnw spring-boot:run
```

The application starts on port `8080` by default.

## API endpoints

### Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`

### Category Management
- `GET /api/categories`
- `GET /api/categories/default`
- `GET /api/categories/custom`
- `GET /api/categories/{id}`
- `POST /api/categories`
- `PUT /api/categories/{id}`
- `DELETE /api/categories/{id}`

### Expense Management
- `POST /api/expenses`
- `GET /api/expenses`
- `GET /api/expenses/{id}`
- `PUT /api/expenses/{id}`
- `DELETE /api/expenses/{id}`
- `GET /api/expenses/filter`
- `GET /api/expenses/search`

### Expense request structure

The `ExpenseRequest` payload supports the following fields:
- `amount`
- `description`
- `note`
- `expenseDate`
- `categoryId`
- `paymentMethod`
- `receiptUrl`
- `status`
- `tagNames`

Example expense creation request:

```json
{
  "amount": 1500.00,
  "description": "Office lunch",
  "note": "Team lunch with client",
  "expenseDate": "2026-07-19",
  "categoryId": 1,
  "paymentMethod": "CARD",
  "receiptUrl": "https://example.com/receipt/1",
  "status": "CONFIRMED",
  "tagNames": ["food", "office", "team"]
}
```

### Expense filtering and search

The expense controller supports:
- pagination with `page`, `size`, `sortBy`, and `sortDir`
- filtering by `categoryId`, `startDate`, `endDate`, `minAmount`, `maxAmount`, `paymentMethod`, `status`, and `tags`
- search by keyword against the expense description

Example filter request:

```http
GET /api/expenses/filter?categoryId=1&startDate=2026-07-01&endDate=2026-07-31&minAmount=100&maxAmount=5000&paymentMethod=CARD&status=CONFIRMED&tags=food,taxi&page=0&size=10&sortBy=expenseDate&sortDir=desc
```

## Tag support

Tags are modeled as a separate `Tag` entity and are connected to `Expense` through a many-to-many relationship.

Important behavior:
- tags are stored in the `tags` table
- tag names are case-insensitive and normalized to lowercase
- if a tag does not exist, it is created automatically during expense creation/update
- the tag list is sent through the expense request as `tagNames`

There is currently no separate dedicated `TagController`. Tag management happens inside the expense API flow.

## Entities involved

### Expense entity
The `Expense` entity includes:
- `expenseId`
- `amount`
- `description`
- `note`
- `expenseDate`
- `paymentMethod`
- `receiptUrl`
- `status`
- `category`
- `user`
- `tags`
- auditing timestamps (`createdAt`, `updatedAt`)

### Tag entity
The `Tag` entity includes:
- `tagId`
- `name` (unique, max length 30)
- `expenses` relationship (mapped by the expense-side many-to-many association)

## Project structure

Key backend packages include:
- `controller` for REST endpoints
- `service` and `service/impl` for business logic
- `repository` for data access
- `entity` for JPA models
- `mapper` for DTO-to-entity conversions
- `security` for JWT and Spring Security setup
- `dto` for request/response models

## Notes

The repository is currently backend-focused and already supports secure authentication, category management, and a working expense module with tag-based organization. Income, budget, recurring expense, and reporting modules are still being added progressively.
