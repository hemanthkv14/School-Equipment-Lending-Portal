import express from 'express';
import { getUserDueLendings } from '../services/dueNotification.js';

const router = express.Router();

router.get('/dueDetails/:userId', async (req, res) => {
    try {
        const userId = req.params.userId;
        const results = await getUserDueLendings(userId);
        res.json({ userId, dueSoonLendings: results });
    } catch (err) {
        console.error("Error fetching due soon lendings:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

export default router;
