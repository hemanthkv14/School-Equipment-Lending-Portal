# SELP Quick Reference Guide

## 🚀 Quick Start

### Prerequisites
- Node.js 20+
- Java 17+
- Maven 3.6+
- PostgreSQL 16+
- Docker & Docker Compose (optional)

### Start All Services (Docker)
```bash
docker-compose up -d
```

### Start All Services (Manual)
```bash
./runAllServices.sh
```

### Individual Service Startup

#### 1. PostgreSQL Database
```bash
# Using Docker
docker-compose up selp-db

# Or start local PostgreSQL
# Make sure .env files are configured
```

#### 2. API Gateway
```bash
cd selp-api-gateway
npm install
npm run dev
# Runs on http://localhost:8081
```

#### 3. Auth Service
```bash
cd selp-auth-service
npm install
npm run dev
# Runs on http://localhost:4000
```

#### 4. Equipment Service
```bash
cd selp-equipment-service
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

#### 5. Notification Service
```bash
cd selp-notification-service
npm install
npm run dev
# Runs on http://localhost:4001
```

#### 6. Client Service (Frontend)
```bash
cd selp-client-service
npm install
npm start
# Runs on http://localhost:3000
```

---

## 📍 Service Ports

| Service | Port | URL |
|---------|------|-----|
| API Gateway | 8081 | http://localhost:8081 |
| Auth Service | 4000 | http://localhost:4000 |
| Equipment Service | 8080 | http://localhost:8080 |
| Notification Service | 4001 | http://localhost:4001 |
| Client Service | 3000 | http://localhost:3000 |
| PostgreSQL | 5434 | localhost:5434 |

---

## 🔑 API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Equipment (`/api/equipment`)
- `GET /api/equipment` - Get all equipment
- `GET /api/equipment/{equipmentId}` - Get equipment by ID
- `POST /api/equipment/add` - Add new equipment
- `PUT /api/equipment/update` - Update equipment
- `DELETE /api/equipment/delete/{equipmentId}` - Delete equipment
- `GET /api/equipment/{equipmentId}/items` - Get items for equipment
- `GET /api/equipment/items` - Get all items
- `PUT /api/equipment/items/update` - Update item
- `DELETE /api/equipment/items/{itemId}` - Delete item

### Lending (`/api/borrowRequests`)
- `GET /api/borrowRequests` - Get all lendings
- `GET /api/borrowRequests/{userId}` - Get user's lendings
- `POST /api/borrowRequests/request` - Create lending request
- `POST /api/borrowRequests/revoke/{lendingRequestId}` - Revoke request
- `POST /api/borrowRequests/approve/{lendingId}?dueDate=...` - Approve lending
- `POST /api/borrowRequests/reject/{lendingId}` - Reject lending
- `POST /api/borrowRequests/return/{lendingId}/{borrowerId}` - Request return
- `POST /api/borrowRequests/acceptReturn/{lendingId}?condition=...` - Accept return

### Users (`/api/user`)
- `GET /api/user` - Get all users
- `GET /api/user/{id}` - Get user by ID
- `POST /api/user` - Create user
- `PUT /api/user/{id}` - Update user
- `DELETE /api/user/{id}` - Delete user

### Notifications (`/api/notifications`)
- `GET /api/notifications/dueDetails/{userId}` - Get user's due lendings

---

## 👥 User Roles

- **STUDENT**: Can browse equipment, request loans, view own requests
- **STAFF**: Can approve/reject requests, accept returns, view all requests
- **ADMIN**: Full access - manage equipment, users, all requests

---

## 🔐 Authentication

### Login Request
```json
POST /api/auth/login
{
  "username": "hemanth-stu",
  "password": "password"
}
```

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 3,
    "username": "hemanth-stu",
    "role": "STUDENT"
  }
}
```

### Using Token
```javascript
// Frontend (localStorage)
localStorage.setItem("token", token);
localStorage.setItem("userId", user.id);
localStorage.setItem("userRole", user.role);

// API Request (currently not validated in backend)
headers: {
  "Authorization": `Bearer ${token}`
}
```

---

## 📊 Database

### Connection Strings

#### Auth Service
```env
PGHOST=localhost
PGUSER=demo
PGPASSWORD=demopw
PGDATABASE=demo_db
PGPORT=5432
```

#### Equipment Service
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/selp_db
SPRING_DATASOURCE_USERNAME=selp_user
SPRING_DATASOURCE_PASSWORD=selp_password
```

#### Notification Service
```env
PG_HOST=localhost
PG_USER=selp_user
PG_PASS=selp_password
PG_DB=selp_db
PG_PORT=5432
```

### Key Tables
- `users` - User accounts
- `categories` - Equipment categories
- `equipment` - Equipment catalog
- `items` - Individual equipment items
- `lendings` - Lending transactions
- `duetracking` - Due date tracking
- `notifications` - Notification records

---

## 🔄 Lending Workflow

### Student Flow
1. Browse equipment → `GET /api/equipment`
2. Select item → `GET /api/equipment/{id}/items`
3. Request loan → `POST /api/borrowRequests/request`
4. View requests → `GET /api/borrowRequests/{userId}`
5. Request return → `POST /api/borrowRequests/return/{lendingId}/{borrowerId}`

### Staff/Admin Flow
1. View requests → `GET /api/borrowRequests`
2. Approve → `POST /api/borrowRequests/approve/{lendingId}?dueDate=2025-12-01T10:00:00`
3. Reject → `POST /api/borrowRequests/reject/{lendingId}`
4. Accept return → `POST /api/borrowRequests/acceptReturn/{lendingId}?condition=GOOD`

### Lending Statuses
- `BORROW_PENDING` - Request submitted, awaiting approval
- `APPROVED` - Request approved, item issued
- `REJECTED` - Request rejected
- `RETURN_PENDING` - Return requested, awaiting staff approval
- `RETURNED` - Item returned and accepted
- `OVERDUE` - Item not returned by due date

---

## 📧 Notifications

### Cron Job
- Runs daily at midnight (configurable via `CRON_SCHEDULE`)
- Checks for items due in 2 days
- Sends email notifications
- Prevents duplicate notifications

### Email Configuration
```env
# SMTP Settings (Notification Service)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-password
```

---

## 🐳 Docker Commands

### Build and Start
```bash
docker-compose up -d
```

### View Logs
```bash
docker-compose logs -f [service-name]
```

### Stop Services
```bash
docker-compose down
```

### Rebuild Service
```bash
docker-compose build [service-name]
docker-compose up -d [service-name]
```

### Access Database
```bash
docker exec -it selp-postgres psql -U selp_user -d selp_db
```

---

## 🧪 Testing

### Test Database Connection
```bash
# From auth service directory
node -e "const pool = require('./src/db'); pool.query('SELECT NOW()', (err, res) => { console.log(err || res.rows); process.exit(); });"
```

### Test API Gateway
```bash
curl http://localhost:8081/api/equipment
```

### Test Equipment Service
```bash
curl http://localhost:8080/api/equipment
```

### Test Auth Service
```bash
curl -X POST http://localhost:4000/login \
  -H "Content-Type: application/json" \
  -d '{"username":"hemanth-stu","password":"password"}'
```

---

## 🔧 Environment Variables

### Auth Service (.env)
```env
PORT=4000
JWT_SECRET=selp_secret
JWT_EXPIRES_IN=1h
PGHOST=localhost
PGUSER=demo
PGPASSWORD=demopw
PGDATABASE=demo_db
PGPORT=5432
```

### Equipment Service (application.properties)
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

### Notification Service (.env)
```env
PORT=4001
CRON_SCHEDULE=0 0 * * *
PG_HOST=localhost
PG_USER=selp_user
PG_PASS=selp_password
PG_DB=selp_db
PG_PORT=5432
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-password
```

---

## 🐛 Common Issues

### Port Already in Use
```bash
# Find process using port
lsof -i :8080
# Kill process
kill -9 <PID>
```

### Database Connection Failed
- Check PostgreSQL is running
- Verify connection strings in .env files
- Check database exists: `psql -l`

### JWT Token Invalid
- Check JWT_SECRET matches across services
- Verify token expiration time
- Check token format in requests

### CORS Errors
- Verify CORS configuration in API Gateway
- Check frontend URL matches CORS origin
- Ensure credentials are included in requests

### Equipment Service Not Starting
- Check Java version: `java -version` (should be 17+)
- Verify Maven is installed: `mvn -version`
- Check database connection in application.properties

---

## 📚 Key Files

### Configuration
- `docker-compose.yml` - Docker services configuration
- `selp-api-gateway/src/index.js` - API Gateway routing
- `selp-equipment-service/src/main/resources/application.properties` - Spring Boot config
- `selp-equipment-service/src/main/resources/schema.sql` - Database schema
- `selp-equipment-service/src/main/resources/data.sql` - Seed data

### Main Application Files
- `selp-auth-service/src/index.js` - Auth service entry point
- `selp-equipment-service/src/main/java/com/school/Application.java` - Spring Boot app
- `selp-notification-service/src/index.js` - Notification service entry point
- `selp-client-service/src/App.js` - React app entry point

### Controllers
- `selp-equipment-service/.../controller/EquipmentController.java`
- `selp-equipment-service/.../controller/LendingController.java`
- `selp-equipment-service/.../controller/UserController.java`

---

## 🎯 Development Tips

1. **Use Postman/Insomnia** for API testing
2. **Check logs** for debugging (each service has console logs)
3. **Database queries** - Use pgAdmin or psql for direct DB access
4. **Frontend DevTools** - Check Network tab for API calls
5. **Spring Boot Actuator** - Consider adding for health checks
6. **Hot Reload** - Use nodemon for Node.js services
7. **Maven Wrapper** - Use `./mvnw` instead of `mvn` for consistency

---

## 📞 Support

### Check Service Status
```bash
# API Gateway
curl http://localhost:8081/api/equipment

# Equipment Service
curl http://localhost:8080/api/equipment

# Auth Service
curl http://localhost:4000/login -X POST -H "Content-Type: application/json" -d '{}'
```

### View Logs
```bash
# Docker logs
docker-compose logs -f

# Individual service logs
# Check terminal where service is running
```

---

**Last Updated**: 2025-01-27

