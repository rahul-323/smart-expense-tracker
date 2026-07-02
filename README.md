# Smart Expense Tracker

Smart Expense Tracker is a Spring Boot backend for a personal finance application. It currently focuses on secure user authentication and the foundational domain model for expense tracking, with the core pieces in place for future expense, income, budget, and recurring-transaction features.

## Overview

This project provides a REST API for:
- user registration and login
- JWT-based authentication
- password hashing and secure access control
- category management foundations
- persistence with MySQL using JPA/Hibernate

## Current implementation status

The backend already includes:
- Spring Security configuration with JWT support
- user registration and login endpoints
- authentication and authorization flow for protected routes
- user, category, and supporting entities
- DTOs, repositories, services, and exception handling

The following areas are still being developed or scaffolded:
- expense CRUD operations
- income tracking
- budget management
- recurring expenses
- analytics and reporting

## Tech stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
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

These values are expected by the application properties in the project.

## Running locally

1. Create a MySQL database named `smart_expense_tracker`
2. Set the required environment variables
3. Run:

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default.

## API endpoints

### Authentication
- POST `/api/auth/register`
- POST `/api/auth/login`

Example request body for registration:

```json
{
  "name": "Rahul",
  "email": "rahul@example.com",
  "password": "password123",
  "phone": "9876543210",
  "monthlyIncome": 50000
}
```

## Project structure

Key backend packages include:
- `controller` for REST endpoints
- `service` and `service/impl` for business logic
- `repository` for data access
- `entity` for JPA models
- `security` for JWT and Spring Security setup
- `dto` for request/response objects

## Notes

This repository is currently a backend-focused starter project. The authentication flow is implemented, while other finance-management modules are being added progressively.
