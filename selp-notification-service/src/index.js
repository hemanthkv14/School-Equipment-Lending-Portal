import dotenv from "dotenv";
import { initializeCronJobs } from "./config/cron.js";
import {startHttpServer} from "./server.js";
dotenv.config();

async function startService() {
    console.log("Starting selp-notification-service ...");
    startHttpServer();
    initializeCronJobs();
    console.log("Cron initialized. Waiting for next schedule...");
}

startService().catch((err) => {
    console.error("Failed to start notification service:", err);
    process.exit(1);
});
