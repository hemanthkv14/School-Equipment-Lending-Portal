import nodemailer from 'nodemailer';
import dotenv from 'dotenv';
import mailTransporter from '../config/mailConfig'
dotenv.config();


export const sendEmail = async (to, subject, text) => {
    try {
        await mailTransporter.sendMail({
            from: process.env.EMAIL_USER,
            to,
            subject,
            html: text,
        });
        console.log(`Email sent to ${to}`);
    } catch (err) {
        console.error(`Failed to send email to ${to}:`, err.message);
    }
};
