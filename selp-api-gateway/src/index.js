const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();

const AUTH_TARGET = 'http://localhost:4000';
const CLIENT_TARGET = 'http://localhost:3000';

// proxy /register* -> auth-service
app.use('/api/auth', createProxyMiddleware({
    target: AUTH_TARGET,
    changeOrigin: true,
    // CRITICAL: This strips '/api/auth' so 4000 receives only '/register'
    pathRewrite: { '^/api/auth': '' }
}));


// serve client (static) via proxy to client-service
app.use('/', createProxyMiddleware({
    target: CLIENT_TARGET,
    changeOrigin: true
}));

const PORT = 8081;
app.listen(PORT, () => console.log(`API Gateway on ${PORT}`));
