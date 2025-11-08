#!/bin/bash

# SELP Docker Startup Script
# This script helps you start the SELP application using Docker Compose

set -e

echo "=========================================="
echo "  SELP - Docker Startup Script"
echo "=========================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "✅ Docker and Docker Compose are installed"
echo ""

# Check if .env file exists
if [ ! -f .env ]; then
    echo "⚠️  .env file not found. Using default environment variables."
    echo "   You can create a .env file to customize configuration."
    echo ""
fi

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo "⚠️  Port $port is already in use. This may cause issues."
        return 1
    fi
    return 0
}

# Check required ports
echo "Checking ports..."
check_port 3000 || echo "  - Port 3000 (Client Service)"
check_port 8080 || echo "  - Port 8080 (Equipment Service)"
check_port 8081 || echo "  - Port 8081 (API Gateway)"
check_port 5434 || echo "  - Port 5434 (Database)"
echo ""

# Ask user if they want to continue
read -p "Do you want to start all services? (y/n) " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 0
fi

echo ""
echo "🚀 Starting SELP services..."
echo ""

# Build and start services
docker-compose up -d --build

echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check service status
echo ""
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "=========================================="
echo "  Services are starting!"
echo "=========================================="
echo ""
echo "🌐 Access the application:"
echo "  - Frontend:        http://localhost:3000"
echo "  - API Gateway:     http://localhost:8081"
echo "  - Equipment API:   http://localhost:8080"
echo ""
echo "📋 Useful commands:"
echo "  - View logs:       docker-compose logs -f"
echo "  - Stop services:   docker-compose down"
echo "  - Restart:         docker-compose restart"
echo ""
echo "📝 Check service logs if any service fails to start:"
echo "  docker-compose logs -f [service-name]"
echo ""
echo "✅ Setup complete!"

