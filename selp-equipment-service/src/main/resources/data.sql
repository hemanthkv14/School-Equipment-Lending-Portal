-- SQL INSERT STATEMENTS
-- Current date assumed for context: 2025-11-08 11:20:07 IST

-- 1. CATEGORIES (No dependencies)
--------------------------------------------------------------------------------

INSERT INTO public.categories (name) VALUES
('Laptops'),
('Projectors'),
('Cameras'),
('Audio Equipment'),
('Networking Gear'),
('Tools'),
('Cables'),
('Peripherals'),
('Furniture'),
('Storage Devices'),
('Robotics'),
('Drones'),
('Scientific Instruments'),
('VR Headsets'),
('Microscopes'),
('Power Supplies'),
('Test Equipment'),
('Security Devices'),
('Studio Lighting'),
('Server Components');

-- 2. USERS (No dependencies - Includes specified users and roles)
--------------------------------------------------------------------------------

INSERT INTO public.users (username, email, password_hash, role, name) VALUES
('admin', 'admin@school.edu', '$2b$10$Gv9pT7TG7NdtcyMgkU.S0.HmGxvbzA26JV/uzcYrVY7ohVutvE.8.', 'admin', 'System Administrator'),
('pratheusha', 'pratheusha.kk@gmail.com', '$2b$10$k6iHJUbqwWPnRY6PR7RyAO0fwgrJtF80Znrgw8Jr.9', 'student', 'Pratheusha');
-- ('pratheusha-admin', 'prat@school.edu', 'hash_admin1', 'ADMIN', 'Pratheusha V'),
-- ('monica-staff', 'monica@school.edu', 'hash_staff1', 'STAFF', 'Monica K'),
-- ('hemanth-stu', 'pratheusha@outlook.com', 'hash_stud1', 'STUDENT', 'Hemanth R'),
-- ('reuben-stu', 'reuben@outlook.com', 'hash_stud2', 'STUDENT', 'Reuben S'),
-- ('asmith', 'asmith@school.edu', 'hash456', 'STAFF', 'Adam Smith'),
-- ('bsmith', 'bsmith@school.edu', 'hash789', 'ADMIN', 'Bill Smith'),
-- ('cwhite', 'cwhite@school.edu', 'hash101', 'STUDENT', 'Carl White'),
-- ('dking', 'dking@school.edu', 'hash102', 'STAFF', 'Dana King'),
-- ('ewalker', 'ewalker@school.edu', 'hash103', 'STUDENT', 'Ella Walker'),
-- ('fgreen', 'fgreen@school.edu', 'hash104', 'ADMIN', 'Fred Green'),
-- ('gharris', 'gharris@school.edu', 'hash105', 'STUDENT', 'Grace Harris'),
-- ('hlee', 'hlee@school.edu', 'hash106', 'STAFF', 'Hank Lee'),
-- ('ijones', 'ijones@school.edu', 'hash107', 'STUDENT', 'Ivy Jones'),
-- ('kjensen', 'kjensen@school.edu', 'hash108', 'STUDENT', 'Kyle Jensen'),
-- ('lmorris', 'lmorris@school.edu', 'hash109', 'STAFF', 'Laura Morris'),
-- ('mnguyen', 'mnguyen@school.edu', 'hash110', 'STUDENT', 'Mike Nguyen'),
-- ('nsanchez', 'nsanchez@school.edu', 'hash111', 'ADMIN', 'Nina Sanchez'),
-- ('obrown', 'obrown@school.edu', 'hash112', 'STUDENT', 'Oscar Brown'),
-- ('privera', 'privera@school.edu', 'hash113', 'STUDENT', 'Pat Rivera'),
-- ('qxu', 'qxu@school.edu', 'hash114', 'STAFF', 'Quinn Xu');
--
-- -- 3. EQUIPMENT (Depends on categories)
-- --------------------------------------------------------------------------------
--
-- INSERT INTO public.equipment (name, category_id, total_quantity, quantity_available, condition, created_at) VALUES
-- ('HP EliteBook G8', 1, 50, 48, 'Good', '2024-01-10 10:00:00'),
-- ('Canon EOS R6', 3, 5, 5, 'New', '2024-03-01 14:00:00'),
-- ('Shure SM58 Mic', 4, 25, 20, 'Fair', '2024-01-20 09:15:00'),
-- ('Cisco Router 4300', 5, 5, 5, 'Good', '2024-04-05 16:30:00'),
-- ('Adjustable Wrench', 6, 40, 40, 'Good', '2023-11-01 08:00:00'),
-- ('HDMI Cable (10ft)', 7, 100, 95, 'Fair', '2023-12-12 12:00:00'),
-- ('Logitech Mouse M100', 8, 30, 25, 'Good', '2024-01-25 13:00:00'),
-- ('Mobile Whiteboard', 9, 8, 8, 'New', '2024-02-20 15:45:00'),
-- ('Arduino Uno Kit', 11, 30, 28, 'Good', '2024-04-10 10:30:00'),
-- ('DJI Mini 4 Pro', 12, 3, 3, 'New', '2024-05-01 11:00:00'),
-- ('Spectrophotometer', 13, 2, 2, 'Good', '2024-05-20 14:00:00'),
-- ('Oculus Quest 2', 14, 10, 9, 'Good', '2024-06-15 12:00:00'),
-- ('Compound Microscope', 15, 15, 15, 'Fair', '2024-06-25 08:30:00'),
-- ('Bench Power Supply', 16, 12, 10, 'Good', '2024-07-10 11:45:00'),
-- ('Digital Oscilloscope', 17, 7, 7, 'New', '2024-07-25 13:30:00'),
-- ('IP Camera Dome', 18, 5, 5, 'Good', '2024-08-01 09:45:00'),
-- ('RAM Stick DDR4 16GB', 20, 20, 20, 'New', '2024-09-01 15:00:00');
--
-- -- 4. ITEMS (Depends on equipment)
-- --------------------------------------------------------------------------------
--
-- INSERT INTO public.items (equipment_id, serial_number, condition, is_available) VALUES
-- (1, '35489012', 'GOOD', FALSE),  -- Laptop 1 (Lent)
-- (1, '88723456', 'NEW', TRUE),   -- Laptop 2
-- (1, '10345678', 'FAIR', TRUE),   -- Laptop 3
-- (2, '71239012', 'GOOD', FALSE),  -- Projector 1 (Lent)
-- (2, '22987654', 'NEW', TRUE),   -- Projector 2
-- (3, '50456789', 'NEW', TRUE),   -- Camera 1
-- (4, '66123456', 'POOR', FALSE),  -- Mic 1 (Lent)
-- (4, '90876543', 'NEW', TRUE),   -- Mic 2
-- (5, '44321098', 'GOOD', TRUE),   -- Router 1
-- (6, '11987654', 'BROKEN', TRUE), -- Wrench 1
-- (7, '70654321', 'FAIR', TRUE),   -- HDMI 1
-- (8, '33210987', 'GOOD', TRUE),   -- Mouse 1
-- (9, '91876543', 'NEW', TRUE),   -- Whiteboard 1
-- (10, '55432109', 'NEW', FALSE),  -- SSD 1 (Lent)
-- (11, '28765432', 'GOOD', TRUE),   -- Arduino 1
-- (12, '19087654', 'NEW', TRUE),   -- Drone 1
-- (13, '82345678', 'FAIR', TRUE),   -- Spectro 1
-- (14, '60987654', 'GOOD', TRUE),   -- VR 1
-- (13, '49102345', 'POOR', TRUE),   -- Microscope 1
-- (14, '77890123', 'NEW', FALSE),  -- Power Supply 1 (Lent)
-- (15, '30567890', 'NEW', TRUE),   -- Oscilloscope 1
-- (16, '92456789', 'GOOD', TRUE),   -- IP Camera 1
-- (17, '41765432', 'NEW', TRUE);   -- RAM Stick 1
--
-- -- 5. LENDINGS (Depends on items and users)
-- --------------------------------------------------------------------------------
-- -- User IDs: 1: Pratheusha(A), 2: Monica(S), 3: Hemanth(T), 4: Reuben(T), 5: Adam(S), 7: Carl(T), 9: Ella(T), 11: Grace(T), 12: Hank(S), 13: Ivy(T), 15: Laura(S)
--
-- INSERT INTO public.lendings (item_id, borrower_id, request_date, approval_status, authorized_by, issue_date) VALUES
-- (1, 3, '2025-10-20 10:00:00', 'APPROVED', 2, '2025-10-21 09:00:00'),
-- (4, 4, '2025-10-25 11:30:00', 'APPROVED', 5, '2025-10-26 10:00:00'),
-- (7, 7, '2025-11-01 14:00:00', 'APPROVED', 8, '2025-11-02 12:00:00'),
-- (10, 9, '2025-09-15 08:00:00', 'RETURNED', 12, '2025-09-16 09:00:00'),
-- (16, 11, '2025-10-05 16:00:00', 'APPROVED', 15, '2025-10-06 14:00:00'),
-- (3, 4, '2025-11-06 11:00:00', 'REJECTED', 2, '2025-11-06 12:00:00'),
-- (5, 7, '2025-11-07 13:00:00', 'APPROVED', 5, '2025-11-07 15:00:00'),
-- (6, 9, '2025-09-01 09:00:00', 'RETURNED', 8, '2025-09-02 11:00:00'),
-- (8, 11, '2025-10-10 15:00:00', 'RETURNED', 12, '2025-10-11 13:00:00'),
-- (9, 13, '2025-08-01 10:00:00', 'RETURNED', 15, '2025-08-02 09:00:00'),
-- (11, 3, '2025-07-20 11:00:00', 'RETURNED', 2, '2025-07-21 10:00:00'),
-- (12, 4, '2025-06-15 14:00:00', 'RETURNED', 5, '2025-06-16 12:00:00'),
-- (13, 7, '2025-05-01 08:00:00', 'RETURNED', 8, '2025-05-02 09:00:00'),
-- (14, 9, '2025-04-10 16:00:00', 'RETURNED', 12, '2025-04-11 14:00:00'),
-- (15, 11, '2025-03-05 10:00:00', 'RETURNED', 15, '2025-03-06 09:00:00'),
-- (17, 13, '2025-02-20 11:00:00', 'RETURNED', 2, '2025-02-21 10:00:00'),
-- (18, 3, '2025-01-15 14:00:00', 'RETURNED', 5, '2025-01-16 12:00:00'),
-- (19, 4, '2024-12-01 08:00:00', 'RETURNED', 8, '2024-12-02 09:00:00'),
-- (20, 7, '2024-11-10 16:00:00', 'RETURNED', 12, '2024-11-11 14:00:00');
--
-- -- 6. DUETRACKING (Depends on lendings)
-- --------------------------------------------------------------------------------
--
-- INSERT INTO public.duetracking (lending_id, due_date, return_date, is_overdue, rejection_date) VALUES
-- (1, '2025-11-05 09:00:00', NULL, TRUE, NULL),
-- (4, '2025-11-15 10:00:00', NULL, FALSE, NULL),
-- (7, '2025-11-12 12:00:00', NULL, FALSE, NULL),
-- (10, '2025-09-25 09:00:00', '2025-09-20 10:00:00', FALSE, NULL),
-- (16, '2025-10-20 14:00:00', NULL, TRUE, NULL),
-- (5, '2025-11-20 15:00:00', NULL, FALSE, NULL),
-- (6, '2025-09-10 11:00:00', '2025-09-08 11:00:00', FALSE, NULL),
-- (8, '2025-10-25 13:00:00', '2025-10-25 15:00:00', FALSE, NULL),
-- (9, '2025-08-10 09:00:00', '2025-08-15 10:00:00', TRUE, NULL),
-- (11, '2025-07-30 10:00:00', '2025-07-30 11:00:00', FALSE, NULL),
-- (12, '2025-06-25 12:00:00', '2025-06-25 12:00:00', FALSE, NULL),
-- (13, '2025-05-10 09:00:00', '2025-05-15 10:00:00', TRUE, NULL),
-- (14, '2025-04-20 14:00:00', '2025-04-18 15:00:00', FALSE, NULL),
-- (15, '2025-03-15 09:00:00', '2025-03-20 10:00:00', TRUE, NULL),
-- (17, '2025-03-01 10:00:00', '2025-02-28 11:00:00', FALSE, NULL),
-- (18, '2025-01-25 12:00:00', '2025-01-26 13:00:00', TRUE, NULL),
-- (19, '2024-12-10 09:00:00', '2024-12-05 10:00:00', FALSE, NULL),
-- (20, '2024-11-20 14:00:00', '2024-11-25 15:00:00', TRUE, NULL);
--
-- -- 7. NOTIFICATIONS (Depends on users and lendings)
-- --------------------------------------------------------------------------------
--
-- INSERT INTO public.notifications (recipient_id, lending_id, type, message, notification_sent, sent_at) VALUES
-- (3, 1, 'OVERDUE', 'Your loan for 35489012 (Laptop) is now overdue.', TRUE, '2025-11-06 09:00:00'),
-- (4, 4, 'REMINDER', 'Your loan for 71239012 (Projector) is due in 7 days.', FALSE, '2025-11-08 10:00:00'),
-- (2, 2, 'NEW_REQUEST', 'A new lending request has been submitted by hemanth.', TRUE, '2025-11-05 10:00:00'),
-- (4, 3, 'REJECTION', 'Your request for a laptop (item 3) was rejected.', TRUE, '2025-11-06 13:00:00'),
-- (11, 16, 'OVERDUE', 'Your loan for 77890123 (Power Supply) is now overdue.', TRUE, '2025-10-21 14:00:00'),
-- (7, 7, 'APPROVAL', 'Your loan for 66123456 (Mic) has been approved.', TRUE, '2025-11-02 12:00:00'),
-- (9, 10, 'RETURNED', 'SSD 1TB has been successfully returned.', TRUE, '2025-09-20 10:05:00'),
-- (12, 5, 'APPROVAL', 'Loan 5 for 22987654 has been approved.', FALSE, '2025-11-07 15:00:00'),
-- (13, 9, 'OVERDUE', 'Loan 9 was returned late. Fine issued.', TRUE, '2025-08-16 10:00:00'),
-- (7, 13, 'OVERDUE', 'Loan 13 was returned late. Fine issued.', TRUE, '2025-05-16 10:00:00'),
-- (11, 15, 'OVERDUE', 'Loan 15 was returned late. Fine issued.', FALSE, '2025-03-21 10:00:00'),
-- (3, 18, 'OVERDUE', 'Loan 18 was returned late. Fine issued.', TRUE, '2025-01-27 10:00:00'),
-- (7, 20, 'OVERDUE', 'Loan 20 was returned late. Fine issued.', TRUE, '2024-11-26 10:00:00'),
-- (2, 1, 'REMINDER', 'You authorized an overdue loan (1) by Hemanth.', TRUE, '2025-11-08 11:00:00'),
-- (15, 16, 'REMINDER', 'You authorized an overdue loan (16) by Grace.', TRUE, '2025-11-08 11:00:00'),
-- (3, 2, 'REMINDER', 'Your loan request (2) is still pending.', TRUE, '2025-11-08 11:10:00');