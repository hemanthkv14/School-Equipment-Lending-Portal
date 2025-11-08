// index.js
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const pool = require('./db');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 4000;
const JWT_SECRET = process.env.JWT_SECRET || "selp_secret";
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || "1h";
const HOST = process.env.HOST || '0.0.0.0'; // Listen on all interfaces for Docker

// Helper: generate token
function genToken(user) {
    return jwt.sign({ id: user.id, username: user.username }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
}

// Register
app.post('/register', async (req, res) => {
    const { username,email, password, role } = req.body;
    if (!username || !password) return res.status(400).json({ error: 'username and password required' });

    try {
        const hash = await bcrypt.hash(password, 10);
        const { rows } = await pool.query(
            'INSERT INTO users(username,email, password_hash, role) VALUES($1,$2,$3,$4) RETURNING user_id, username',
            [username,email, hash, role || null]
        );
        const user = rows[0];
        res.status(201).json({ user });
    } catch (err) {
        if (err.code === '23505') { // unique violation
            return res.status(409).json({ error: 'username already registered' });
        }
        console.error(err);
        res.status(500).json({ error: 'internal error' });
    }
});

// Login
app.post('/login', async (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) return res.status(400).json({ error: 'username and password required' });

    try {
        const { rows } = await pool.query('SELECT user_id, username, password_hash,role FROM users WHERE username=$1', [username]);
        const user = rows[0];
        if (!user) return res.status(401).json({ error: 'invalid credentials' });

        const ok = await bcrypt.compare(password, user.password_hash);
        if (!ok) return res.status(401).json({ error: 'invalid credentials' });

        const token = genToken(user);
        res.json({ token, user: { id: user.user_id, username: user.username, role: user.role } });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'internal error' });
    }
});


app.listen(PORT, HOST, () => {
    console.log(`Auth service listening on ${HOST}:${PORT}`);
});
