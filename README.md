# Smart Parking Management System

Full-stack local application using Java 17, Spring Boot 3, Spring Security + JWT, MySQL 8, and Angular 19.

**This version uses locally installed MySQL. It does not use Docker and contains no deployment setup.**

## Requirements
- JDK 17+
- Maven 3.9+
- Node.js 20.11+ and npm
- MySQL Server 8+ (MySQL Workbench is optional)

## 1. Set up MySQL locally
1. Start your MySQL Server service.
2. Open MySQL Workbench or the MySQL command-line client.
3. Run `database/01-create-database.sql`, or execute:

```sql
CREATE DATABASE smart_parking_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. Open `backend/src/main/resources/application.yml` and set the database username/password to match your local MySQL installation.

Default local configuration:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_parking_app?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: root
```

If your MySQL root password is not `root`, replace it with your actual password. The application creates its tables and demo records automatically on first successful startup.

## 2. Run Spring Boot
From the project root:

```bash
cd backend
mvn clean spring-boot:run
```

Backend: `http://localhost:8080`  
Health check: `http://localhost:8080/api/health`

Demo accounts:
- Admin: `admin@parking.local` / `Admin@123`
- Operator: `operator@parking.local` / `Operator@123`

## 3. Run Angular
Open a second terminal:

```bash
cd frontend
npm install
npm start
```

Open `http://localhost:4200`. The Angular dev server proxies `/api` to Spring Boot on port 8080. The sign-in page includes a separate customer registration form. New customers can register with full name, email, and password; Admin and Operator roles are not offered on that form.

## Build and test
Backend tests:

```bash
cd backend
mvn clean test
```

Frontend build check:

```bash
cd frontend
npm install
npm run build
```

## API overview
All endpoints except `/api/auth/login` and `/api/health` require `Authorization: Bearer <JWT>`.

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/health` | Public | Health check |
| POST | `/api/auth/login` | Public | Login and receive JWT |
| POST | `/api/auth/register` | Public | Register a customer account only; backend always assigns USER |
| GET | `/api/auth/me` | Authenticated | Current user |
| GET | `/api/dashboard` | ADMIN, OPERATOR | Operations dashboard metrics |
| GET | `/api/lots` | Authenticated | List parking lots |
| POST | `/api/lots` | ADMIN | Create lot and slots |
| GET | `/api/slots` | Authenticated | List parking slots |
| POST | `/api/parking/entry` | ADMIN, OPERATOR | Vehicle check-in |
| POST | `/api/parking/{id}/exit` | ADMIN, OPERATOR | Checkout and calculate fee |
| GET | `/api/parking/sessions` | ADMIN, OPERATOR | Session history |
| GET | `/api/parking/active` | ADMIN, OPERATOR | Active sessions |
| POST | `/api/payments/{sessionId}` | ADMIN, OPERATOR | Record payment |
| GET | `/api/audit` | ADMIN | Audit event history |

## Business rules
- One active session per vehicle.
- Entry assigns the first available slot matching the vehicle type.
- Checkout rounds parking time up to billable hours, with a one-hour minimum.
- Checkout frees the parking slot.
- Payment is recorded separately; this demo records method and amount but does not charge a bank account or UPI handle.
- Public registration creates USER accounts only; the role is assigned by the backend and cannot be supplied by the browser.
- ADMIN can create lots and view audit events.
- ADMIN and OPERATOR can check vehicles in/out and record payments.

## Troubleshooting
- MySQL connection refused: ensure MySQL Server is running on port 3306.
- Access denied: check `username` and `password` in `application.yml`.
- Unknown database: run the SQL script in `database/`.
- Port 8080 busy: stop the other process or change Spring's `server.port` and the Angular proxy target.
- Port 4200 busy: run `ng serve --port 4201 --proxy-config proxy.conf.json`.
- Maven not found: install Maven and add its `bin` directory to PATH.
- Node/npm not found: install Node.js 20.11+ and reopen the terminal.

## Project structure
```text
smart-parking-management-system/
├── backend/       Spring Boot REST API, JWT security, JPA entities/services
├── frontend/      Angular standalone application
├── database/      Local MySQL database creation script
└── README.md
```

## University demo checklist
1. Start local MySQL and create the database.
2. Start Spring Boot and Angular.
3. Sign in as Admin and show dashboard metrics.
4. Add a parking lot and slots (or use the seeded lot).
5. Sign in as Operator and register a vehicle.
6. Show its slot becoming OCCUPIED.
7. Check out the vehicle and show the calculated fee.
8. Record payment and show it in history.
9. Explain JPA entities, REST APIs, JWT filter, and role-based access.

This is a local academic/demo foundation. Run it, understand the modules, customize it, and follow your university's rules for disclosing AI assistance.
