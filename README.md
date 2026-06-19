# PeerIsland Order Management System

Spring Boot microservice for customer and order management.

## Requirement

Scenario: Build the backend for an E-commerce Order Processing System. The system should allow customers to place orders, track order status, and perform basic order operations.

Core features:

- Create an order: Customers should be able to place an order with multiple items.
- Retrieve order details: The system should allow fetching order details by order ID.
- Update order status: Orders support `PENDING`, `PROCESSING`, `SHIPPED`, and `DELIVERED`. A background job automatically updates `PENDING` orders to `PROCESSING` every 5 minutes.
- List all orders: Retrieve all orders, optionally filtered by status.
- Cancel an order: Customers should be able to cancel an order only when it is still in `PENDING` status.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Security with JWT
- Spring Data JPA
- H2 database for local runtime
- MapStruct
- Lombok
- Springdoc OpenAPI / Swagger UI

## Run Locally

```bash
cd order-management
./mvnw spring-boot:run
```

The service runs on:

```text
http://localhost:8081/order-management
```

## Local Database

The local profile uses H2:

```text
jdbc:h2:file:./data/order-management
```

H2 console:

```text
http://localhost:8081/order-management/h2-console
```

Use:

```text
JDBC URL: jdbc:h2:file:./data/order-management
User: sa
Password: <empty>
```

## Seeded User

`src/main/resources/data.sql` seeds roles and one customer so you can log in immediately.

```text
Name: Sharath Nair
Email: sharath.nair@peerisland.com
Password: Password@123
Role: CUSTOMER
Status: ACTIVE
```

## Swagger

Swagger UI:

```text
http://localhost:8081/order-management/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8081/order-management/v3/api-docs
```

## Postman

Import this collection into Postman:

```text
postman/order-management.postman_collection.json
```

The collection has these variables:

- `baseUrl`: defaults to `http://localhost:8081/order-management`
- `customerEmail`: defaults to `sharath.nair@peerisland.com`
- `customerPassword`: defaults to `Password@123`
- `accessToken`: set automatically after login
- `refreshToken`: set automatically after login
- `customerId`: defaults to `1`
- `orderId`: set automatically after create order

Recommended Postman flow:

1. Run `Public / Login`.
2. Run customer or order APIs.
3. Run `Public / Refresh Token` when needed.

## API Summary

Public APIs:

```text
GET  /public/ping
POST /public/v1/login
POST /public/v1/refresh-token
PUT  /public/v1/reset-password/{username}
```

Customer APIs:

```text
POST  /v1/customers
GET   /v1/customers?page=0&size=10
GET   /v1/customers/{customerId}
PUT   /v1/customers/{customerId}
PATCH /v1/customers/change-account-status?customerId=1&active=true
```

Order APIs:

```text
POST  /v1/orders
GET   /v1/orders/{orderId}
GET   /v1/orders?page=0&size=10
GET   /v1/orders?page=0&size=10&status=PENDING
PATCH /v1/orders/{orderId}/status
PATCH /v1/orders/{orderId}/cancel
```

Supported order statuses:

```text
PENDING
PROCESSING
SHIPPED
DELIVERED
CANCELLED
```

The background scheduler moves `PENDING` orders to `PROCESSING` every 5 minutes.

## Authentication

Protected endpoints require:

```text
Authorization: Bearer <accessToken>
requestId: <any unique request id>
```

## Build And Test

```bash
cd order-management
./mvnw test
```
