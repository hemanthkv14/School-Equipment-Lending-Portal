const cors = require('cors');
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();


const AUTH_TARGET = 'http://localhost:4000';
const CLIENT_TARGET = 'http://localhost:3000';
const EQUIPMENT_TARGET = 'http://localhost:8080';
const NOTIFICATION_TARGET = 'http://localhost:4001';


app.use(cors({
    origin: 'http://localhost:3000',
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    credentials: true,
}));


// proxy /register* -> auth-service
app.use('/api/auth', createProxyMiddleware({
    target: AUTH_TARGET,
    changeOrigin: true,
     pathRewrite: { '^/api/auth': '' } // removing /api/auth from the url
}));

app.use(
    "/api/notifications",
    createProxyMiddleware({
        target: "http://localhost:4001",
        changeOrigin: true,
        pathRewrite: { "^/api/notifications": "" },
    })
);

app.use('/api', createProxyMiddleware({
    target: EQUIPMENT_TARGET,
    changeOrigin: true,
    pathRewrite: { '^': '/api' } ,
    timeout: 300000,       // 5 minutes – client socket timeout
    proxyTimeout: 300000
}));


app.use((req, res, next) => {
    console.log("Notification service received request:", req.path);
    next();
});


// serve client (static) via proxy to client-service
app.use('/', createProxyMiddleware({
    target: CLIENT_TARGET,
    changeOrigin: true
}));

const PORT = 8081;
app.listen(PORT, () => console.log(`API Gateway on ${PORT}`));
