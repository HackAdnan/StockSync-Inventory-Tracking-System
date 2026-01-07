# 🏷️ StockSync - Inventory Tracking System

A scalable inventory management REST API system designed to support 500+ stores, a central product catalog, store-specific inventory, and historical stock movement tracking.

---

## 🧠 Design Decisions & Architecture

### 🏗️  Layered MVC Architecture

Inventory Tracking System v2,  uses a Modular Layered Architecture based on MVC principles, often called Layered MVC.

- **Controller Layer** – Exposes RESTful endpoints.
- **Service Layer** – Handles business logic.
- **DTOs** – Clean data transfer across layers.
- **Repository Layer** – Interfaces with the relational DB (PostgreSQL).
- **Entities** – Mapped with JPA (Hibernate) to manage persistence.


---

## 📝 Assumptions

- A **product** is globally unique (central catalog) but its **stock** is store-specific.
- Admin users are authorized to modify resources, while regular users have read-only access.
- Stock movements are logged for **auditing**, **reporting**, and **analytics**.
- Date inputs follow the ISO-8601 format: `yyyy-MM-dd'T'HH:mm:ss`.
- Filtering by **store**, **product**, and **date range** is a common use-case.

---

## 🔌 REST API Design (Highlights)

### 📦 `/api/products`
- `GET /api/products` — List all products
- `GET /api/products/{id}` — Get product by ID
- `POST /api/products` — Create a product (admin)
- `PUT /api/products/{id}` — Update a product (admin)
- `DELETE /api/products/{id}` — Delete a product (admin)

### 🏬 `/api/stores`
- `GET /api/stores` — List all stores
- `GET /api/stores/{id}` — Get store by ID
- `POST /api/stores` — Create a store (admin)

### 📊 `/api/inventory`
- `GET /user/all` — Get all inventory (user)
- `GET /user/store/{storeId}` — Get inventory for store (user)
- `PUT /admin/product/{productId}/store/{storeId}` — Update stock at a store (admin)

### 🔄 `/api/stock-movements`
- `GET /` — List all stock movements
- `GET /store/{storeId}/product/{productId}` — Get movements by store + product
- `GET /date-range?startDate=...&endDate=...` — Movements in date range
- `POST /` — Log a stock movement (admin)

---

## 🔁 Evolution Rationale v1 →  v2

| Version | Focus | Summary                                                                                                                                                  |
|--------|-------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **V1** | Foundation | Basic CRUD operations for Products and Stores only using Flat-file local storage method.                                          |
| **V2** | Scalability & Stock | Centralized product catalog, store-specific stock, stock movements, and filtering/reporting, Role-based access, authentication, API Access rate limiting |

---

## 🔐 Security & Request Throttling 

- Basic Auth using Spring Secuirty
- Admin/User role separation
- It simply waits (throttles) the request until a token is available, up to a safe delay (e.g., 500ms) to avoid server strain using Bucket4j Algorithm Library.
- Password is encrypted using BCryptPasswordEncoder()
---

## 🗃️ Tech Stack

- Java 17
- Spring Boot 3.x
- PostgreSQL
- Hibernate (JPA)
- Postman (for API testing)

---

## 🚀 Setup Instructions

1. Clone the repo.
2. Configure `application.properties` with your DB credentials.
3. Run the Spring Boot application.
4. Import the provided Postman Collection to test endpoints.

---

