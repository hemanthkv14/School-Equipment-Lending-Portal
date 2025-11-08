# SELP - School Equipment Lending Platform

A microservices-based platform for managing school equipment lending with role-based access control (Student, Staff, Admin).

## 🚀 Quick Start with Docker (Recommended)

The easiest way to run the entire application is using Docker Compose:

### Prerequisites
- Docker (version 20.10+)
- Docker Compose (version 2.0+)

### Steps

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd SELP
   ```

2. **Start all services**:
   ```bash
   docker-compose up -d
   ```

3. **Access the application**:
   - **Frontend**: http://localhost:3000
   - **API Gateway**: http://localhost:8081
   - **Equipment Service**: http://localhost:8080

4. **Check service status**:
   ```bash
   docker-compose ps
   ```

5. **View logs**:
   ```bash
   docker-compose logs -f
   ```

6. **Stop services**:
   ```bash
   docker-compose down
   ```

### What's Included
- ✅ PostgreSQL Database (with schema and seed data)
- ✅ Equipment Service (Spring Boot)
- ✅ Auth Service (Node.js)
- ✅ Notification Service (Node.js)
- ✅ API Gateway (Node.js)
- ✅ Client Service (React)

All services are automatically configured and connected via Docker networking.

**For detailed Docker setup instructions, see [DOCKER_SETUP.md](./DOCKER_SETUP.md)**

---

## 🛠️ Manual Setup (Development)

If you prefer to run services manually without Docker:

### Prerequisites
- Node.js 20+
- Java 17+
- Maven 3.6+
- PostgreSQL 16+

### Database Setup

1. **Create PostgreSQL database**:
   ```sql
   CREATE DATABASE selp_db;
   CREATE USER selp_user WITH PASSWORD 'selp_password';
   GRANT ALL PRIVILEGES ON DATABASE selp_db TO selp_user;
   ```

2. **Run schema and seed data**:
   ```bash
   psql -U selp_user -d selp_db -f selp-equipment-service/src/main/resources/schema.sql
   psql -U selp_user -d selp_db -f selp-equipment-service/src/main/resources/data.sql
   ```

### Run Services

#### 1. API Gateway
```bash
cd selp-api-gateway
npm install
npm run dev
# Runs on http://localhost:8081
```

#### 2. Auth Service
```bash
cd selp-auth-service
npm install
npm run dev
# Runs on http://localhost:4000
```

#### 3. Equipment Service
```bash
cd selp-equipment-service
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

#### 4. Notification Service
```bash
cd selp-notification-service
npm install
npm run dev
# Runs on http://localhost:4001
```

#### 5. Client Service (Frontend)
```bash
cd selp-client-service
npm install
npm start
# Runs on http://localhost:3000
```

### Environment Variables

Create `.env` files in each service directory:

**selp-auth-service/.env**:
```env
PORT=4000
JWT_SECRET=your_secret_key
JWT_EXPIRES_IN=1h
PGHOST=localhost
PGUSER=selp_user
PGPASSWORD=selp_password
PGDATABASE=selp_db
PGPORT=5432
```

**selp-notification-service/.env**:
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

**selp-client-service/.env**:
```env
REACT_APP_API_URL=http://localhost:8081
```

### Using the Script

Alternatively, use the provided script to start all services:
```bash
chmod +x runAllServices.sh
./runAllServices.sh
```

---

## 📋 Service Ports

| Service | Port | URL |
|---------|------|-----|
| API Gateway | 8081 | http://localhost:8081 |
| Auth Service | 4000 | http://localhost:4000 |
| Equipment Service | 8080 | http://localhost:8080 |
| Notification Service | 4001 | http://localhost:4001 |
| Client Service | 3000 | http://localhost:3000 |
| PostgreSQL | 5434 | localhost:5434 |

---

## 👥 User Roles

- **STUDENT**: Browse equipment, request loans, view own requests
- **STAFF**: Approve/reject requests, accept returns, view all requests
- **ADMIN**: Full access - manage equipment, users, all requests

### Default Users (from seed data)
- Admin: `pratheusha-admin` / `hash_admin1`
- Staff: `monica-staff` / `hash_staff1`
- Student: `hemanth-stu` / `hash_stud1`

**Note**: In production, change these default passwords!

---

## 🗄️ Database Schema

The database includes the following tables:
- `users` - User accounts with roles
- `categories` - Equipment categories
- `equipment` - Equipment catalog
- `items` - Individual equipment items
- `lendings` - Lending transactions
- `duetracking` - Due date tracking
- `notifications` - Notification records

See `selp-equipment-service/src/main/resources/schema.sql` for full schema.

---

## 📧 Notifications

The notification service runs a cron job that:
- Checks for items due in 2 days
- Sends email notifications to borrowers
- Prevents duplicate notifications
- Logs all notifications in the database

Configure SMTP settings in `.env` file for email functionality.

---

## 🔧 Database Modifications

The following changes were made to the database schema:

```sql
-- Make user_id auto-generated
ALTER TABLE users ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE users ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY;

-- Allow longer messages in notifications
ALTER TABLE notifications ALTER COLUMN message TYPE TEXT;
```

---

## 📚 Documentation

- **[DOCKER_SETUP.md](./DOCKER_SETUP.md)** - Complete Docker setup guide
- **[PROJECT_ANALYSIS.md](./PROJECT_ANALYSIS.md)** - Detailed project analysis
- **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)** - Quick reference guide

---

## 🐛 Troubleshooting

### Docker Issues
- See [DOCKER_SETUP.md](./DOCKER_SETUP.md) troubleshooting section
- Check service logs: `docker-compose logs -f [service-name]`
- Verify ports are not in use: `lsof -i :3000`

### Database Connection Issues
- Verify PostgreSQL is running
- Check connection credentials in `.env` files
- Ensure database exists and schema is loaded

### Service Communication Issues
- Verify all services are running
- Check API Gateway is routing correctly
- Verify CORS settings in API Gateway

---

## 🔒 Security Notes

**⚠️ This is a development setup. For production:**

1. Change all default passwords
2. Use strong JWT secrets
3. Enable HTTPS
4. Configure proper firewall rules
5. Use environment variables for secrets
6. Enable authentication/authorization in all services
7. Regularly update dependencies

---

## 📝 License

[Your License Here]

---

## 👥 Contributors

[Your Contributors Here]

---

## 🆘 Support

For issues and questions:
1. Check the documentation files
2. Review service logs
3. Check GitHub issues (if applicable)

---

**Last Updated**: 2025-01-27
