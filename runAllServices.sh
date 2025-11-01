#!/bin/bash

# ---------------------------
# Start API Gateway
# ---------------------------
echo "Starting API Gateway..."
cd selp-api-gateway || exit
npm run dev &
API_GATEWAY_PID=$!
echo "API Gateway running on http://localhost:8081"
cd ..

# ---------------------------
# Start Auth Service
# ---------------------------
echo "Starting Auth Service..."
cd selp-auth-service || exit
npm run dev &
AUTH_SERVICE_PID=$!
echo "Auth Service running on http://localhost:4000"
cd ..

# ---------------------------
# Start Client Service
# ---------------------------
echo "Starting Client Service..."
cd selp-client-service || exit
npm start &
CLIENT_SERVICE_PID=$!
echo "Client Service running on http://localhost:3000"
cd ..

# ---------------------------
# Start Equipment Service (Spring Boot)
# ---------------------------
echo "Starting Equipment Service..."
cd selp-equipment-service || exit
mvn spring-boot:run &
EQUIPMENT_SERVICE_PID=$!
echo "Equipment Service running on http://localhost:8080"
cd ..

# ---------------------------
# Start Notification Service (Cron)
# ---------------------------
echo "Starting Notification Service..."
cd selp-notification-service || exit
npm run dev &
NOTIFICATION_SERVICE_PID=$!
echo "Notification Service running..."
cd ..

# ---------------------------
# Wait for all services
# ---------------------------
echo "All services started. Press Ctrl+C to stop."
wait $API_GATEWAY_PID $AUTH_SERVICE_PID $CLIENT_SERVICE_PID $EQUIPMENT_SERVICE_PID $NOTIFICATION_SERVICE_PID
