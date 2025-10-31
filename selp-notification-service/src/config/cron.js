import cron from "node-cron";
import { checkDueDatesAndNotify } from "../services/dueDateChecker.js";

export function initializeCronJobs() {
    const schedule = process.env.CRON_SCHEDULE || "0 0 * * *";
    cron.schedule(schedule, async () => {
        console.log("🕐 Running due date check...");
        await checkDueDatesAndNotify();
    });
}
