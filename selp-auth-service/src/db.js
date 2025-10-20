const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
    host: process.env.PGHOST || 'postgres',
    user: process.env.PGUSER || 'demo',
    password: process.env.PGPASSWORD || 'demopw',
    database: process.env.PGDATABASE || 'demo_db',
    port: process.env.PGPORT ? parseInt(process.env.PGPORT) : 5432,
});

module.exports = pool;
