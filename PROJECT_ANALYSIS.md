# SELP (School Equipment Lending Platform) - Complete Project Analysis

## 📋 Executive Summary

**SELP** is a microservices-based School Equipment Lending Platform that enables students to borrow equipment, staff to manage requests, and admins to oversee the entire system. The platform manages equipment inventory, lending requests, due dates, and notifications.

---

## 🏗️ Architecture Overview

### Architecture Pattern: **Microservices**

The system is divided into 5 independent services:

1. **API Gateway** (Port 8081) - Entry point, routes requests
2. **Auth Service** (Port 4000) - Authentication & authorization
3. **Equipment Service** (Port 8080) - Core business logic (Spring Boot)
4. **Notification Service** (Port 4001) - Email notifications & cron jobs
5. **Client Service** (Port 3000) - React frontend

### Communication Flow:
```
Client → API Gateway → {Auth Service | Equipment Service | Notification Service}
```

---

## 🛠️ Technology Stack

### Backend Services:
- **Node.js 20** (Auth, API Gateway, Notification services)
- **Spring Boot 3.2.3** (Equipment service)
- **Java 17** (Equipment service)
- **PostgreSQL 16** (Database)

### Frontend:
- **React 19.2.0**
- **React Router DOM 7.9.4**
- **Tailwind CSS 3.4.18**
- **Axios 1.12.2**

### Infrastructure:
- **Docker & Docker Compose**
- **Express.js** (Node.js services)
- **JWT** (Authentication)
- **bcrypt** (Password hashing)
- **node-cron** (Scheduled tasks)
- **Nodemailer** (Email notifications)

### Database:
- **PostgreSQL 16** with:
  - JPA/Hibernate (Spring Boot)
  - Native SQL queries (Node.js services)

---

## 📦 Service Breakdown

### 1. **API Gateway** (`selp-api-gateway`)
**Purpose**: Central entry point, routes requests to appropriate services

**Technology**: Node.js + Express + http-proxy-middleware

**Endpoints**:
- `/api/auth/*` → Auth Service (port 4000)
- `/api/notifications/*` → Notification Service (port 4001)
- `/api/*` → Equipment Service (port 8080)
- `/` → Client Service (port 3000)

**Features**:
- CORS enabled for `localhost:3000`
- Request proxying with path rewriting
- Timeout configuration (5 minutes for equipment service)

**Port**: 8081

---

### 2. **Auth Service** (`selp-auth-service`)
**Purpose**: User authentication and registration

**Technology**: Node.js + Express + JWT + bcrypt + PostgreSQL

**Endpoints**:
- `POST /register` - Register new user
- `POST /login` - Authenticate user, returns JWT token

**Features**:
- Password hashing with bcrypt (salt rounds: 10)
- JWT token generation (1 hour expiry)
- User role management (STUDENT, STAFF, ADMIN)
- Database: Shared PostgreSQL `users` table

**Database Connection**:
- Uses environment variables for DB config
- Default: `localhost:5432`, `demo_db`

**Security**:
- JWT_SECRET from environment (default: "selp_secret")
- Password validation
- Unique username/email constraints

**Port**: 4000

---

### 3. **Equipment Service** (`selp-equipment-service`)
**Purpose**: Core business logic for equipment, items, lendings, and users

**Technology**: Spring Boot 3.2.3 + Java 17 + JPA/Hibernate + PostgreSQL

**Main Controllers**:
1. **EquipmentController** (`/api/equipment`)
   - `GET /api/equipment` - Get all equipment
   - `POST /api/equipment/add` - Add new equipment
   - `PUT /api/equipment/update` - Update equipment
   - `DELETE /api/equipment/delete/{equipmentId}` - Delete equipment catalog
   - `GET /api/equipment/{equipmentId}` - Get equipment by ID
   - `GET /api/equipment/{equipmentId}/items` - Get items for equipment
   - `GET /api/equipment/items` - Get all items
   - `PUT /api/equipment/items/update` - Update item
   - `DELETE /api/equipment/items/{itemId}` - Delete item unit

2. **LendingController** (`/api/borrowRequests`)
   - `POST /api/borrowRequests/request` - Create lending request
   - `POST /api/borrowRequests/revoke/{lendingRequestId}` - Revoke request
   - `POST /api/borrowRequests/approve/{lendingId}` - Approve lending
   - `POST /api/borrowRequests/reject/{lendingId}` - Reject lending
   - `POST /api/borrowRequests/return/{lendingId}/{borrowerId}` - Request return
   - `POST /api/borrowRequests/acceptReturn/{lendingId}` - Accept return
   - `GET /api/borrowRequests` - Get all lendings
   - `GET /api/borrowRequests/{userId}` - Get user's lendings

3. **CategoryController** (`/api/categories`)
   - Category management endpoints

4. **DueTrackingController** (`/api/duetracking`)
   - Due date tracking endpoints

5. **UserController** (`/api/user`)
   - `POST /api/user` - Create user
   - `GET /api/user` - Get all users
   - `GET /api/user/{id}` - Get user by ID
   - `PUT /api/user/{id}` - Update user
   - `DELETE /api/user/{id}` - Delete user

**Entities**:
- `User` - Users with roles (ADMIN, STAFF, STUDENT)
- `Category` - Equipment categories
- `Equipment` - Equipment catalog (name, category, quantities)
- `Item` - Individual equipment items (serial numbers, condition)
- `Lending` - Lending transactions
- `DueTracking` - Due date tracking
- `Notification` - Notification records

**Enums**:
- `LendingStatus`: BORROW_PENDING, APPROVED, REJECTED, OVERDUE, RETURN_PENDING, RETURNED
- `UserRole`: ADMIN, STUDENT, STAFF
- `ItemCondition`: NEW, GOOD, FAIR, POOR, BROKEN
- `NotificationType`: (various types)

**Business Logic**:
- Equipment inventory management
- Item availability tracking
- Lending workflow (request → approve/reject → issue → return)
- Due date tracking
- Automatic quantity updates
- Notification creation

**Security**:
- **Currently disabled** - SecurityConfig permits all requests
- No JWT validation in controllers
- Hardcoded user ID in LendingController (`getAuthenticatedUserId()` returns `2L`)

**Port**: 8080

---

### 4. **Notification Service** (`selp-notification-service`)
**Purpose**: Email notifications and due date reminders

**Technology**: Node.js + Express + node-cron + Nodemailer + PostgreSQL

**Features**:
- **Cron Job**: Checks due dates daily (default: `0 0 * * *` - midnight)
- Sends email reminders 2 days before due date
- Prevents duplicate notifications
- Logs notifications in database

**Endpoints**:
- `GET /dueDetails/:userId` - Get user's due lendings

**Cron Logic**:
- Queries `duetracking` table for upcoming due dates
- Calculates days until due (target: 2 days)
- Sends email if notification not already sent
- Updates `notifications` table with status

**Email Service**:
- Uses Nodemailer
- HTML email templates
- Configurable SMTP settings via environment variables

**Port**: 4001

---

### 5. **Client Service** (`selp-client-service`)
**Purpose**: React frontend application

**Technology**: React 19.2.0 + React Router + Tailwind CSS + Axios

**Main Components**:

1. **Authentication**:
   - `Login.js` - User login
   - `RegisterUser.js` - User registration (students self-register, admins register others)

2. **Student Views**:
   - `StudentDashboard.js` - Student dashboard
   - `EquipmentList.js` - Browse available equipment
   - `MyRequests.js` - View own lending requests
   - `RequestForm.js` - Submit lending request

3. **Staff Views**:
   - `StaffDashboard.js` - Staff dashboard
   - `AdminRequests.js` - Manage lending requests (approve/reject)

4. **Admin Views**:
   - `AdminDashboard.js` - Admin dashboard
   - `AdminEquipmentList.js` - Manage equipment catalog
   - `AdminRequests.js` - Manage all requests
   - `RegisterUser.js` - Register new users (any role)

5. **Shared**:
   - `Navbar.js` - Navigation bar
   - `ProtectedRoute.js` - Route protection based on role
   - `RoleRedirect.js` - Role-based redirects

**Routing**:
- Role-based route protection
- Automatic redirects based on user role
- Public routes: `/`, `/register-student`

**State Management**:
- LocalStorage for token, userId, userRole, username
- No global state management (Redux/Context)

**API Integration**:
- Base URL: `http://localhost:8081` (API Gateway)
- Axios for HTTP requests
- Token stored in localStorage

**Port**: 3000

---

## 🗄️ Database Schema

### Tables:

1. **categories**
   - `category_id` (PK, auto-increment)
   - `name` (unique)

2. **users**
   - `user_id` (PK, GENERATED ALWAYS AS IDENTITY)
   - `username` (unique)
   - `email` (unique)
   - `password_hash`
   - `role` (CHECK: STUDENT, STAFF, ADMIN)
   - `token`
   - `created_at`
   - `name`

3. **equipment**
   - `equipment_id` (PK, auto-increment)
   - `name`
   - `category_id` (FK → categories)
   - `total_quantity` (CHECK: >= 0)
   - `quantity_available` (CHECK: >= 0)
   - `condition`
   - `created_at`

4. **items**
   - `item_id` (PK, auto-increment)
   - `equipment_id` (FK → equipment)
   - `serial_number` (unique)
   - `condition` (CHECK: NEW, GOOD, FAIR, POOR, BROKEN)
   - `is_available` (boolean)

5. **lendings**
   - `lending_id` (PK, auto-increment)
   - `item_id` (FK → items, unique)
   - `borrower_id` (FK → users)
   - `request_date`
   - `approval_status` (enum)
   - `authorized_by` (FK → users)
   - `issue_date`

6. **duetracking**
   - `due_id` (PK, auto-increment)
   - `lending_id` (FK → lendings, unique)
   - `due_date`
   - `return_date`
   - `is_overdue` (boolean)
   - `rejection_date`

7. **notifications**
   - `notification_id` (PK, auto-increment)
   - `recipient_id` (FK → users)
   - `lending_id` (FK → lendings, nullable)
   - `type`
   - `message` (TEXT)
   - `created_at`
   - `notification_sent` (boolean)
   - `sent_at`

### Relationships:
- Equipment → Category (Many-to-One)
- Item → Equipment (Many-to-One)
- Lending → Item (One-to-One)
- Lending → User (borrower) (Many-to-One)
- Lending → User (authorized_by) (Many-to-One)
- DueTracking → Lending (One-to-One)
- Notification → User (Many-to-One)
- Notification → Lending (Many-to-One, nullable)

---

## 🔐 Security Analysis

### Current Security Status:

#### ✅ Implemented:
1. **Password Hashing**: bcrypt with salt rounds 10
2. **JWT Tokens**: Token-based authentication
3. **Role-Based Access**: Frontend route protection
4. **CORS**: Configured for frontend origin
5. **Input Validation**: Equipment validation, SQL constraints

#### ❌ Missing/Issues:
1. **No JWT Validation in Backend**: Equipment service doesn't validate JWT tokens
2. **Hardcoded User ID**: `LendingController.getAuthenticatedUserId()` returns `2L`
3. **No Authorization Checks**: Backend doesn't verify user roles
4. **Security Config Disabled**: Spring Security permits all requests
5. **No Rate Limiting**: API endpoints vulnerable to abuse
6. **No Input Sanitization**: SQL injection risks in Node.js services
7. **Secrets in Code**: Default JWT secret hardcoded
8. **No HTTPS**: All communication over HTTP
9. **Token Storage**: JWT stored in localStorage (XSS vulnerable)

### Recommendations:
1. Implement JWT validation middleware in Equipment service
2. Extract user ID from JWT token
3. Add role-based authorization checks
4. Enable Spring Security with proper configuration
5. Add rate limiting (e.g., express-rate-limit)
6. Use parameterized queries (already done in most places)
7. Move secrets to environment variables
8. Implement HTTPS in production
9. Consider httpOnly cookies for token storage

---

## 🚀 Deployment

### Docker Compose Setup:
- **PostgreSQL**: Port 5434 (host) → 5432 (container)
- **Equipment Service**: Port 8080
- **API Gateway**: Port 4000 (host) → 3000 (container)
- **Client Service**: Port 80 (host) → 3000 (container)
- **Auth Service**: No exposed ports (internal only)
- **Notification Service**: No exposed ports (internal only)

### Environment Variables Required:
- **Auth Service**: `PGHOST`, `PGUSER`, `PGPASSWORD`, `PGDATABASE`, `PGPORT`, `JWT_SECRET`, `JWT_EXPIRES_IN`
- **Equipment Service**: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- **Notification Service**: `PG_HOST`, `PG_USER`, `PG_PASS`, `PG_DB`, `PG_PORT`, `CRON_SCHEDULE`, SMTP config

### Manual Startup (Development):
1. Start PostgreSQL (or use docker-compose)
2. Run `runAllServices.sh` script
3. Or start each service individually:
   - API Gateway: `npm run dev` (port 8081)
   - Auth Service: `npm run dev` (port 4000)
   - Equipment Service: `mvn spring-boot:run` (port 8080)
   - Client Service: `npm start` (port 3000)
   - Notification Service: `npm run dev` (port 4001)

---

## 📊 Data Flow

### Lending Request Flow:
1. **Student** → Frontend: Select equipment item
2. **Frontend** → API Gateway: `POST /api/borrowRequests/request`
3. **API Gateway** → Equipment Service: Forward request
4. **Equipment Service**:
   - Validate item availability
   - Create `Lending` record (status: BORROW_PENDING)
   - Clear previous due dates
5. **Staff/Admin** → Frontend: View pending requests
6. **Staff/Admin** → API Gateway: `POST /api/borrowRequests/approve/{lendingId}`
7. **Equipment Service**:
   - Update lending status to APPROVED
   - Decrease `quantity_available`
   - Mark item as unavailable
   - Create due tracking record
   - Create notification

### Return Flow:
1. **Student** → Frontend: Request return
2. **Frontend** → API Gateway: `POST /api/borrowRequests/return/{lendingId}/{borrowerId}`
3. **Equipment Service**: Update status to RETURN_PENDING
4. **Staff/Admin** → Frontend: Accept return with condition
5. **Equipment Service**:
   - Update item condition
   - Mark item as available
   - Increase `quantity_available`
   - Update status to RETURNED
   - Update return date in due tracking

### Notification Flow:
1. **Cron Job** (daily): Check `duetracking` table
2. **Calculate**: Days until due date
3. **If 2 days or less**: Send email notification
4. **Update**: `notifications` table with sent status
5. **Prevent**: Duplicate notifications

---

## 🐛 Known Issues & Limitations

### Critical Issues:
1. **No Authentication in Equipment Service**: Anyone can access endpoints
2. **Hardcoded User ID**: Approval/return operations use fake user ID
3. **No Authorization**: No role-based access control in backend
4. **Security Config Disabled**: Spring Security not enforcing any rules

### Functional Issues:
1. **Port Mismatch**: API Gateway listens on 8081 but docker-compose maps to 4000
2. **Database Connection**: Auth service uses different DB config than equipment service
3. **No Error Handling**: Some endpoints lack proper error responses
4. **No Validation**: Some DTOs lack validation annotations
5. **CORS Issues**: May have issues with different origins

### Code Quality Issues:
1. **Inconsistent Error Handling**: Different services handle errors differently
2. **No Logging**: Limited logging across services
3. **No Tests**: No unit or integration tests
4. **Hardcoded Values**: Several hardcoded configuration values
5. **No API Documentation**: No Swagger/OpenAPI documentation

### Performance Issues:
1. **N+1 Queries**: Potential N+1 query problems in JPA
2. **No Caching**: No caching layer for frequently accessed data
3. **No Pagination**: Lists return all records
4. **No Rate Limiting**: API endpoints vulnerable to abuse

---

## 🔧 Improvement Recommendations

### Security:
1. ✅ Implement JWT validation in Equipment service
2. ✅ Add Spring Security configuration
3. ✅ Extract user from JWT token
4. ✅ Add role-based authorization
5. ✅ Use httpOnly cookies for tokens
6. ✅ Add rate limiting
7. ✅ Implement HTTPS
8. ✅ Add input validation and sanitization

### Functionality:
1. ✅ Add pagination for lists
2. ✅ Add search and filtering
3. ✅ Add equipment images
4. ✅ Add email templates
5. ✅ Add notification preferences
6. ✅ Add fine calculation for overdue items
7. ✅ Add equipment reservation system
8. ✅ Add equipment maintenance tracking

### Code Quality:
1. ✅ Add unit tests
2. ✅ Add integration tests
3. ✅ Add API documentation (Swagger)
4. ✅ Add logging (structured logging)
5. ✅ Add error handling middleware
6. ✅ Add request validation
7. ✅ Add code formatting (Prettier, Checkstyle)

### Infrastructure:
1. ✅ Fix port mappings in docker-compose
2. ✅ Add health checks
3. ✅ Add monitoring (Prometheus, Grafana)
4. ✅ Add logging aggregation (ELK stack)
5. ✅ Add CI/CD pipeline
6. ✅ Add database migrations (Flyway/Liquibase)
7. ✅ Add environment-specific configs

---

## 📈 Project Statistics

### Codebase Size:
- **Java Files**: ~35+ files
- **JavaScript/React Files**: ~15+ files
- **SQL Files**: 2 (schema.sql, data.sql)
- **Configuration Files**: ~10+ files

### Database:
- **Tables**: 7
- **Entities**: 7
- **Enums**: 4
- **Seed Data**: 20 categories, 20 users, 17 equipment, 23 items, 19 lendings

### API Endpoints:
- **Equipment Service**: ~20+ endpoints
- **Auth Service**: 2 endpoints
- **Notification Service**: 1 endpoint
- **Total**: ~23+ endpoints

### Frontend Routes:
- **Public Routes**: 2
- **Protected Routes**: 8+
- **Total**: 10+ routes

---

## 🎯 Use Cases Supported

### Student:
1. ✅ Register account
2. ✅ Login
3. ✅ Browse equipment
4. ✅ Request equipment loan
5. ✅ View own requests
6. ✅ Request return
7. ✅ Receive notifications

### Staff:
1. ✅ Login
2. ✅ View pending requests
3. ✅ Approve/reject requests
4. ✅ Accept returns
5. ✅ View all lendings

### Admin:
1. ✅ Login
2. ✅ Register users (any role)
3. ✅ Manage equipment catalog
4. ✅ Add/update/delete equipment
5. ✅ Manage items
6. ✅ View all requests
7. ✅ Approve/reject requests
8. ✅ Accept returns
9. ✅ View all users

---

## 📝 Conclusion

SELP is a **well-structured microservices application** with clear separation of concerns. The architecture is sound, but **security is the primary concern**. The system needs:

1. **Immediate**: JWT validation, authorization, and proper user extraction
2. **Short-term**: Error handling, logging, and testing
3. **Long-term**: Performance optimization, monitoring, and scalability improvements

The codebase shows good understanding of microservices patterns, but production readiness requires addressing security and operational concerns.

---

## 📚 Additional Notes

### Database Initialization:
- Schema is created via `schema.sql`
- Seed data is loaded via `data.sql`
- Both run automatically on PostgreSQL container startup

### Development Setup:
- Requires Node.js 20+
- Requires Java 17+
- Requires Maven 3.6+
- Requires PostgreSQL 16+

### Testing:
- No test files found
- No test configuration
- Recommended: Add JUnit (Java), Jest (React), Supertest (Node.js)

---

**Analysis Date**: 2025-01-27
**Analyzed By**: AI Assistant
**Project Version**: 1.0-SNAPSHOT

