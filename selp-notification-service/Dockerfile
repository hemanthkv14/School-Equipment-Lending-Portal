FROM node:20-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies (production only)
# Using npm install instead of npm ci for better compatibility with lock files
RUN npm install --omit=dev --no-audit --no-fund

# Copy source code
COPY . .

# Expose port
EXPOSE 4001

# Start the application
CMD ["node", "src/index.js"]

