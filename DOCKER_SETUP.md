# Docker Setup Guide for SELP

This guide will help you dockerize and run the SELP (School Equipment Lending Platform) application using Docker and Docker Compose.

## 🐳 Prerequisites

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Git** (to clone the repository)

### Verify Installation
```bash
docker --version
docker-compose --version
```

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd SELP
```

### 2. (Optional) Configure Environment Variables
Create a `.env` file in the root directory (optional, defaults are provided):
```bash
# Copy the example (if available)
cp .env.example .env

# Or create manually
nano .env
```

Example `.env` file:
```env
# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_change_in_production
JWT_EXPIRES_IN=1h

# Cron Schedule (Notification Service)
CRON_SCHEDULE=0 0 * * *

# SMTP Configuration (for email notifications)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-app-password
```

### 3. Start All Services
```bash
docker-compose up -d
```

This will:
- Build all Docker images
- Start all containers
- Initialize the database with schema and seed data
- Set up networking between services

### 4. Verify Services are Running
```bash
docker-compose ps
```

You should see all 6 services running:
- `selp-postgres` (Database)
- `selp-equipment` (Equipment Service)
- `selp-auth` (Auth Service)
- `selp-notification` (Notification Service)
- `selp-gateway` (API Gateway)
- `selp-client` (Client/Frontend)

### 5. Access the Application
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8081
- **Equipment Service**: http://localhost:8080
- **Database**: localhost:5434

## 📋 Service Details

### Service Ports
| Service | Container Port | Host Port | URL |
|---------|---------------|-----------|-----|
| Database | 5432 | 5434 | localhost:5434 |
| Equipment Service | 8080 | 8080 | http://localhost:8080 |
| Auth Service | 4000 | - | Internal only |
| Notification Service | 4001 | - | Internal only |
| API Gateway | 8081 | 8081 | http://localhost:8081 |
| Client Service | 3000 | 3000 | http://localhost:3000 |

### Service Communication
All services communicate via Docker network `selp-network`:
- Services use service names (e.g., `selp-db`, `selp-auth-service`) instead of `localhost`
- Internal communication happens within the Docker network
- External access is through exposed ports

## 🔧 Common Docker Commands

### Start Services
```bash
# Start all services in detached mode
docker-compose up -d

# Start specific service
docker-compose up -d selp-equipment-service

# Start with rebuild
docker-compose up -d --build
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes database data)
docker-compose down -v

# Stop specific service
docker-compose stop selp-equipment-service
```

### View Logs
```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f selp-equipment-service

# View last 100 lines
docker-compose logs --tail=100 selp-api-gateway
```

### Rebuild Services
```bash
# Rebuild all services
docker-compose build

# Rebuild specific service
docker-compose build selp-client-service

# Rebuild and restart
docker-compose up -d --build selp-api-gateway
```

### Database Access
```bash
# Access PostgreSQL container
docker exec -it selp-postgres psql -U selp_user -d selp_db

# Run SQL commands
docker exec -it selp-postgres psql -U selp_user -d selp_db -c "SELECT * FROM users;"

# Backup database
docker exec selp-postgres pg_dump -U selp_user selp_db > backup.sql

# Restore database
docker exec -i selp-postgres psql -U selp_user selp_db < backup.sql
```

### Service Status
```bash
# Check service status
docker-compose ps

# Check service health
docker-compose ps --format json

# View resource usage
docker stats
```

## 🛠️ Troubleshooting

### Services Won't Start

1. **Check Docker is running**:
   ```bash
   docker ps
   ```

2. **Check port conflicts**:
   ```bash
   # Check if ports are in use
   lsof -i :3000
   lsof -i :8080
   lsof -i :8081
   lsof -i :5434
   ```

3. **View service logs**:
   ```bash
   docker-compose logs selp-equipment-service
   ```

4. **Check service dependencies**:
   ```bash
   # Ensure database is healthy
   docker-compose ps selp-db
   ```

### Database Connection Issues

1. **Wait for database to be ready**:
   ```bash
   # Check database health
   docker-compose ps selp-db
   ```

2. **Verify database credentials**:
   - Username: `selp_user`
   - Password: `selp_password`
   - Database: `selp_db`
   - Port: `5432` (internal), `5434` (external)

3. **Reset database**:
   ```bash
   # Stop services
   docker-compose down -v
   
   # Remove database volume
   docker volume rm selp_postgres_data
   
   # Start services again
   docker-compose up -d
   ```

### Build Issues

1. **Clear Docker cache**:
   ```bash
   docker-compose build --no-cache
   ```

2. **Remove old images**:
   ```bash
   docker-compose down
   docker system prune -a
   docker-compose up -d --build
   ```

3. **Check Dockerfile syntax**:
   ```bash
   docker build -t test-image ./selp-api-gateway
   ```

### Network Issues

1. **Recreate network**:
   ```bash
   docker-compose down
   docker network prune
   docker-compose up -d
   ```

2. **Check service connectivity**:
   ```bash
   # From API Gateway container
   docker exec -it selp-gateway ping selp-auth-service
   ```

### React App Not Loading

1. **Check API URL configuration**:
   - Verify `REACT_APP_API_URL` in `docker-compose.yml`
   - Should be `http://localhost:8081` for browser access

2. **Check CORS settings**:
   - Verify `CLIENT_ORIGIN` in API Gateway environment

3. **View client logs**:
   ```bash
   docker-compose logs -f selp-client-service
   ```

## 📝 Environment Variables

### Database (selp-db)
- `POSTGRES_USER`: selp_user
- `POSTGRES_PASSWORD`: selp_password
- `POSTGRES_DB`: selp_db

### Equipment Service (selp-equipment-service)
- `SPRING_DATASOURCE_URL`: jdbc:postgresql://selp-db:5432/selp_db
- `SPRING_DATASOURCE_USERNAME`: selp_user
- `SPRING_DATASOURCE_PASSWORD`: selp_password

### Auth Service (selp-auth-service)
- `PORT`: 4000
- `JWT_SECRET`: (from .env or default)
- `JWT_EXPIRES_IN`: 1h
- `PGHOST`: selp-db
- `PGUSER`: selp_user
- `PGPASSWORD`: selp_password
- `PGDATABASE`: selp_db
- `PGPORT`: 5432

### Notification Service (selp-notification-service)
- `PORT`: 4001
- `CRON_SCHEDULE`: (from .env or default: "0 0 * * *")
- `PG_HOST`: selp-db
- `PG_USER`: selp_user
- `PG_PASS`: selp_password
- `PG_DB`: selp_db
- `PG_PORT`: 5432
- `SMTP_HOST`: (from .env)
- `SMTP_PORT`: (from .env)
- `SMTP_USER`: (from .env)
- `SMTP_PASS`: (from .env)

### API Gateway (selp-api-gateway)
- `PORT`: 8081
- `AUTH_SERVICE_URL`: http://selp-auth-service:4000
- `EQUIPMENT_SERVICE_URL`: http://selp-equipment-service:8080
- `NOTIFICATION_SERVICE_URL`: http://selp-notification-service:4001
- `CLIENT_SERVICE_URL`: http://selp-client-service:3000
- `CLIENT_ORIGIN`: http://localhost:3000

### Client Service (selp-client-service)
- `REACT_APP_API_URL`: http://localhost:8081
- `HOST`: 0.0.0.0

## 🔒 Security Considerations

### Production Deployment

1. **Change default passwords**:
   - Update database credentials
   - Change JWT_SECRET
   - Update all default passwords

2. **Use environment variables**:
   - Never commit `.env` files
   - Use Docker secrets or environment variable managers

3. **Enable HTTPS**:
   - Use reverse proxy (nginx, traefik)
   - Configure SSL certificates

4. **Network security**:
   - Don't expose database port externally
   - Use firewall rules
   - Limit service exposure

5. **Update dependencies**:
   - Regularly update Docker images
   - Keep packages up to date
   - Scan for vulnerabilities

## 📦 Data Persistence

### Database Volume
The database data is persisted in a Docker volume:
```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect selp_postgres_data

# Backup volume
docker run --rm -v selp_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz /data
```

### Backup Strategy
1. **Regular backups**: Set up cron jobs to backup database
2. **Volume backups**: Backup Docker volumes
3. **Export data**: Use `pg_dump` for database exports

## 🚢 Production Deployment

### Recommended Setup

1. **Use Docker Swarm or Kubernetes** for orchestration
2. **Set up reverse proxy** (nginx, traefik) for HTTPS
3. **Configure monitoring** (Prometheus, Grafana)
4. **Set up logging** (ELK stack, Loki)
5. **Enable health checks** for all services
6. **Use secrets management** for sensitive data
7. **Configure auto-scaling** based on load
8. **Set up CI/CD pipeline** for deployments

### Docker Compose Overrides
Create `docker-compose.prod.yml` for production:
```yaml
version: '3.8'
services:
  selp-client-service:
    build:
      args:
        REACT_APP_API_URL: https://api.yourdomain.com
    environment:
      REACT_APP_API_URL: https://api.yourdomain.com
```

Use with:
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [Node.js Docker Image](https://hub.docker.com/_/node)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)

## 🆘 Getting Help

If you encounter issues:
1. Check service logs: `docker-compose logs -f [service-name]`
2. Verify service status: `docker-compose ps`
3. Check network connectivity: `docker network inspect selp_selp-network`
4. Review this guide's troubleshooting section
5. Check GitHub issues (if applicable)

---

**Last Updated**: 2025-01-27

