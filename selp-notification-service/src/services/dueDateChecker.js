import pool from '../config/db.js';
import { sendEmail } from './notificationSender.js';

export const checkDueDatesAndNotify = async () => {
    console.log('🔔 Checking for upcoming due dates...');

    try {
        const result = await pool.query(`
            SELECT
                u.user_id AS recipient_id,
                u.email,
                u.username,
                d.due_date,
                l.lending_id
            FROM duetracking d
                     JOIN lendings l ON l.lending_id = d.lending_id
                     JOIN users u ON u.user_id = l.borrower_id
        `);

        const now = new Date();

        for (const row of result.rows) {
            const dueDate = new Date(row.due_date);
            const diffDays = Math.ceil((dueDate - now) / (1000 * 60 * 60 * 24));

            if (diffDays <= 2 && diffDays >= 0) {
                const message = `
                      <div style="font-family: Arial, sans-serif; line-height: 1.5; color: #333;">
                        <h2 style="color: #2E86C1;">Reminder: Item Due Soon</h2>
                        <p>Hi <strong>${row.username}</strong>,</p>
                        <p>Your borrowed item with <strong>Lending ID: ${row.lending_id}</strong> is due on <strong>${dueDate.toDateString()}</strong>.</p>
                        <p>Please return it on time to avoid penalties.</p>
                        <br>
                        <p>Thank you!</p>
                        <p style="font-size: 0.9em; color: #888;">School Equipment Lending Portal</p>
                      </div>
                    `;
                // Check if a notification for this lending+recipient+type already exists and was sent
                const existing = await pool.query(
                    `SELECT * FROM notifications 
           WHERE lending_id = $1 AND recipient_id = $2 AND type = 'due_reminder'`,
                    [row.lending_id, row.recipient_id]
                );

                if (existing.rows.length > 0 && existing.rows[0].notification_sent) {
                    console.log(`ℹ️ Notification already sent for lending_id ${row.lending_id} to ${row.email}`);
                    continue;
                }

                try {
                    // Send the email
                    await sendEmail(
                        row.email,
                        `Reminder: Lending ID ${row.lending_id} due on ${dueDate.toDateString()}`,
                        message
                    );

                    // Insert or update notification record
                    if (existing.rows.length > 0) {
                        await pool.query(
                            `UPDATE notifications 
               SET message = $1, notification_sent = TRUE, sent_at = NOW() 
               WHERE notification_id = $2`,
                            [message, existing.rows[0].notification_id]
                        );
                    } else {
                        await pool.query(
                            `INSERT INTO notifications (message, notification_sent, sent_at, type, lending_id, recipient_id)
               VALUES ($1, TRUE, NOW(), 'due_reminder', $2, $3)`,
                            [message, row.lending_id, row.recipient_id]
                        );
                    }

                    console.log(`Notification logged and email sent to ${row.email}`);
                } catch (err) {
                    console.error(`Error sending notification to ${row.email}:`, err.message);

                    // Log failed attempt
                    await pool.query(
                        `INSERT INTO notifications (message, notification_sent, type, lending_id, recipient_id)
             VALUES ($1, FALSE, 'due_reminder', $2, $3)`,
                        [message, row.lending_id, row.recipient_id]
                    );
                }
            }
        }
    } catch (err) {
        console.error('Error in notification service:', err.message);
    }
};
