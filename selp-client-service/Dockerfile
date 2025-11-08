# Development build - uses React dev server
FROM node:20-alpine

WORKDIR /app

# Install dependencies for build tools
RUN apk add --no-cache python3 make g++

# Copy package files
COPY package*.json ./

# Install all dependencies (including dev dependencies for react-scripts)
# Using npm install instead of npm ci for better compatibility with lock files
RUN npm install --no-audit --no-fund

# Copy source code
COPY . .

# Set environment variables
# HOST=0.0.0.0 allows React dev server to listen on all interfaces (required for Docker)
# This will be overridden by docker-compose
ENV REACT_APP_API_URL=http://localhost:8081
ENV HOST=0.0.0.0
ENV WDS_SOCKET_HOST=localhost
ENV WDS_SOCKET_PORT=3000

# Expose port
EXPOSE 3000

# Start React development server
CMD ["npm", "start"]
