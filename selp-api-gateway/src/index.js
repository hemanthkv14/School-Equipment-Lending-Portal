const cors = require('cors');
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();


const AUTH_TARGET = 'http://localhost:4000';
const CLIENT_TARGET = 'http://localhost:3000';
const EQUIPMENT_TARGET = 'http://localhost:8080';


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

app.use('/api', createProxyMiddleware({
    target: 'http://localhost:8080',
    changeOrigin: true,
    pathRewrite: { '^': '/api' }   // prepend /api to everything after mount
}));



// app.use('/api', (req, res, next) => {
//     console.log(`Gateway forwarding: ${req.method} ${req.originalUrl}`);
//     next();
// });

// serve client (static) via proxy to client-service
app.use('/', createProxyMiddleware({
    target: CLIENT_TARGET,
    changeOrigin: true
}));

const PORT = 8081;
app.listen(PORT, () => console.log(`API Gateway on ${PORT}`));
