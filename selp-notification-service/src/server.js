import express from "express";
import notificationRoutes from "./routes/notificationRoutes.js";

export function startHttpServer() {
    const app = express();
    const PORT = process.env.PORT || 4001;

    app.use(express.json());
    app.use("", notificationRoutes);

    app.listen(PORT, () => {
        console.log(`Notification service API running on port ${PORT}`);
    });
}
