const cors = require('cors');
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();

// Use environment variables with fallbacks for local development
const AUTH_TARGET = process.env.AUTH_SERVICE_URL || 'http://localhost:4000';
const CLIENT_TARGET = process.env.CLIENT_SERVICE_URL || 'http://localhost:3000';
const EQUIPMENT_TARGET = process.env.EQUIPMENT_SERVICE_URL || 'http://localhost:8080';
const NOTIFICATION_TARGET = process.env.NOTIFICATION_SERVICE_URL || 'http://localhost:4001';
const CLIENT_ORIGIN = process.env.CLIENT_ORIGIN || 'http://localhost:3000';
const PORT = process.env.PORT || 8081;

app.use(cors({
    origin: CLIENT_ORIGIN,
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    credentials: true,
}));


// Auth service proxy
app.use('/api/auth', createProxyMiddleware({
    target: AUTH_TARGET,
    changeOrigin: true,
    pathRewrite: { '^/api/auth': '' }, // removing /api/auth from the url
    onProxyReq: (proxyReq, req, res) => {
        console.log(`[Gateway] ${req.method} ${req.originalUrl} -> Auth Service`);
    },
    onError: (err, req, res) => {
        console.error(`[Gateway] Error proxying to Auth Service:`, err.message);
        res.status(500).json({ error: 'Internal server error' });
    }
}));

// Notification service proxy
app.use('/api/notifications', createProxyMiddleware({
    target: NOTIFICATION_TARGET,
    changeOrigin: true,
    pathRewrite: { "^/api/notifications": "" },
    onProxyReq: (proxyReq, req, res) => {
        console.log(`[Gateway] ${req.method} ${req.originalUrl} -> Notification Service`);
    },
    onError: (err, req, res) => {
        console.error(`[Gateway] Error proxying to Notification Service:`, err.message);
        res.status(500).json({ error: 'Internal server error' });
    }
}));

// Equipment service proxy (catch-all for /api routes after auth and notifications)
app.use('/api', createProxyMiddleware({
    target: EQUIPMENT_TARGET,
    changeOrigin: true,
    pathRewrite: { '^': '/api' } ,
    timeout: 300000,       // 5 minutes – client socket timeout
    proxyTimeout: 300000,
    onProxyReq: (proxyReq, req, res) => {
        console.log(`[Gateway] ${req.method} ${req.originalUrl} -> Equipment Service`);
    },
    onError: (err, req, res) => {
        console.error(`[Gateway] Error proxying to Equipment Service:`, err.message);
        res.status(500).json({ error: 'Internal server error' });
    }
}));

// serve client (static) via proxy to client-service (catch-all for non-API routes)
app.use('/', createProxyMiddleware({
    target: CLIENT_TARGET,
    changeOrigin: true,
    onProxyReq: (proxyReq, req, res) => {
        console.log(`[Gateway] ${req.method} ${req.originalUrl} -> Client Service`);
    }
}));

app.listen(PORT, () => {
    console.log(`API Gateway listening on port ${PORT}`);
    console.log(`Proxying to:`);
    console.log(`  - Auth Service: ${AUTH_TARGET}`);
    console.log(`  - Equipment Service: ${EQUIPMENT_TARGET}`);
    console.log(`  - Notification Service: ${NOTIFICATION_TARGET}`);
    console.log(`  - Client Service: ${CLIENT_TARGET}`);
});
