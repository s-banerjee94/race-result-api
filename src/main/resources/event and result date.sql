INSERT INTO event (id, event_name, event_date, event_description, organizer_name, organizer_website, city, state, country, image_url) VALUES
(1, 'Mumbai Marathon 2025', '2025-01-19', 'The biggest marathon in India featuring full marathon, half marathon, and 10K runs through the heart of Mumbai', 'Mumbai Marathon Foundation', 'https://mumbaimarathon.com', 'Mumbai', 'Maharashtra', 'India', 'https://example.com/mumbai-marathon.jpg'),
(2, 'Delhi Half Marathon', '2025-02-15', 'Annual half marathon event in the capital city with categories for all age groups', 'Delhi Running Club', 'https://delhihalfmarathon.com', 'New Delhi', 'Delhi', 'India', 'https://example.com/delhi-marathon.jpg'),
(3, 'Bangalore Midnight Marathon', '2025-03-08', 'Unique midnight marathon experience through the IT capital of India', 'Bangalore Runners Association', 'https://bangaloremidnightmarathon.com', 'Bangalore', 'Karnataka', 'India', 'https://example.com/bangalore-marathon.jpg'),
(4, 'Kolkata Heritage Run', '2025-04-12', 'Run through the cultural heritage sites of Kolkata in this 21K and 10K event', 'Kolkata Road Runners', 'https://kolkataheritagerun.com', 'Kolkata', 'West Bengal', 'India', 'https://example.com/kolkata-run.jpg'),
(5, 'Chennai Beach Marathon', '2025-05-20', 'Scenic marathon along the Marina Beach, one of the longest beaches in the world', 'Chennai Runners Club', 'https://chennaibeachmarathon.com', 'Chennai', 'Tamil Nadu', 'India', 'https://example.com/chennai-marathon.jpg');

INSERT INTO event_category (id, category_name, flag_off_time, event_id) VALUES
-- Mumbai Marathon categories
(1, 'Full Marathon (42K)', '05:00:00', 1),
(2, 'Half Marathon (21K)', '05:30:00', 1),
(3, '10K Run', '06:00:00', 1),
-- Delhi Half Marathon categories
(4, 'Half Marathon (21K)', '06:00:00', 2),
(5, '10K Run', '06:30:00', 2),
-- Bangalore Midnight Marathon categories
(6, 'Full Marathon (42K)', '23:00:00', 3),
(7, 'Half Marathon (21K)', '23:30:00', 3),
-- Kolkata Heritage Run categories
(8, 'Half Marathon (21K)', '05:30:00', 4),
(9, '10K Run', '06:00:00', 4),
-- Chennai Beach Marathon categories
(10, 'Half Marathon (21K)', '05:00:00', 5),
(11, '10K Run', '05:30:00', 5);

INSERT INTO result (id, bib_number, participant_name, gender, race_category, age_category, over_all_rank, gender_rank, age_category_rank, chip_time, gun_time, event_id, event_category_id) VALUES
-- Mumbai Marathon Results (Event 1)
(1, 'MUM001', 'Rahul Sharma', 'M', 'Full Marathon', '18-35', '1', '1', '1', '02:45:30', '02:46:00', 1, 1),
(2, 'MUM002', 'Priya Patel', 'F', 'Full Marathon', '18-35', '2', '1', '1', '02:52:15', '02:52:45', 1, 1),
(3, 'MUM003', 'Amit Kumar', 'M', 'Half Marathon', '36-50', '1', '1', '1', '01:15:20', '01:15:50', 1, 2),
(4, 'MUM004', 'Sneha Reddy', 'F', 'Half Marathon', '18-35', '2', '1', '1', '01:18:45', '01:19:10', 1, 2),
(5, 'MUM005', 'Vikram Singh', 'M', '10K Run', '18-35', '1', '1', '1', '00:38:20', '00:38:35', 1, 3),
(6, 'MUM006', 'Anjali Mehta', 'F', '10K Run', '36-50', '2', '1', '1', '00:42:10', '00:42:30', 1, 3),
(7, 'MUM007', 'Rajesh Nair', 'M', 'Full Marathon', '51+', '3', '2', '1', '03:10:45', '03:11:20', 1, 1),
(8, 'MUM008', 'Kavita Joshi', 'F', 'Half Marathon', '36-50', '3', '2', '1', '01:25:30', '01:26:00', 1, 2),
(9, 'MUM009', 'Suresh Gupta', 'M', '10K Run', '51+', '3', '2', '1', '00:45:15', '00:45:40', 1, 3),
(10, 'MUM010', 'Deepa Shah', 'F', 'Full Marathon', '36-50', '4', '2', '1', '03:15:20', '03:16:00', 1, 1),

-- Delhi Half Marathon Results (Event 2)
(11, 'DEL001', 'Arjun Verma', 'M', 'Half Marathon', '18-35', '1', '1', '1', '01:12:45', '01:13:00', 2, 4),
(12, 'DEL002', 'Neha Kapoor', 'F', 'Half Marathon', '18-35', '2', '1', '1', '01:16:20', '01:16:45', 2, 4),
(13, 'DEL003', 'Manish Pandey', 'M', '10K Run', '36-50', '1', '1', '1', '00:36:50', '00:37:10', 2, 5),
(14, 'DEL004', 'Ritu Bansal', 'F', '10K Run', '18-35', '2', '1', '1', '00:39:30', '00:39:50', 2, 5),
(15, 'DEL005', 'Karan Malhotra', 'M', 'Half Marathon', '36-50', '3', '2', '1', '01:18:10', '01:18:35', 2, 4),
(16, 'DEL006', 'Pooja Agarwal', 'F', 'Half Marathon', '36-50', '4', '2', '1', '01:22:40', '01:23:10', 2, 4),
(17, 'DEL007', 'Sandeep Yadav', 'M', '10K Run', '18-35', '3', '2', '2', '00:40:25', '00:40:50', 2, 5),
(18, 'DEL008', 'Meera Saxena', 'F', '10K Run', '51+', '4', '2', '1', '00:48:15', '00:48:40', 2, 5),
(19, 'DEL009', 'Rohit Chauhan', 'M', 'Half Marathon', '51+', '5', '3', '1', '01:28:30', '01:29:00', 2, 4),
(20, 'DEL010', 'Anita Dubey', 'F', 'Half Marathon', '18-35', '6', '3', '2', '01:24:50', '01:25:20', 2, 4),

-- Bangalore Midnight Marathon Results (Event 3)
(21, 'BLR001', 'Arun Krishnan', 'M', 'Full Marathon', '18-35', '1', '1', '1', '02:48:20', '02:48:50', 3, 6),
(22, 'BLR002', 'Divya Iyer', 'F', 'Full Marathon', '18-35', '2', '1', '1', '02:58:45', '02:59:15', 3, 6),
(23, 'BLR003', 'Prashanth Rao', 'M', 'Half Marathon', '36-50', '1', '1', '1', '01:14:30', '01:14:55', 3, 7),
(24, 'BLR004', 'Lakshmi Murthy', 'F', 'Half Marathon', '18-35', '2', '1', '1', '01:17:20', '01:17:50', 3, 7),
(25, 'BLR005', 'Naveen Kumar', 'M', 'Full Marathon', '36-50', '3', '2', '1', '03:05:10', '03:05:45', 3, 6),
(26, 'BLR006', 'Shilpa Hegde', 'F', 'Half Marathon', '36-50', '3', '2', '1', '01:21:40', '01:22:10', 3, 7),
(27, 'BLR007', 'Ganesh Pai', 'M', 'Half Marathon', '51+', '4', '2', '1', '01:26:15', '01:26:45', 3, 7),
(28, 'BLR008', 'Radha Shetty', 'F', 'Full Marathon', '36-50', '4', '2', '1', '03:20:30', '03:21:10', 3, 6),
(29, 'BLR009', 'Sunil Gowda', 'M', 'Half Marathon', '18-35', '5', '3', '2', '01:19:50', '01:20:20', 3, 7),
(30, 'BLR010', 'Asha Kulkarni', 'F', 'Half Marathon', '51+', '6', '3', '1', '01:32:40', '01:33:15', 3, 7),

-- Kolkata Heritage Run Results (Event 4)
(31, 'KOL001', 'Sourav Das', 'M', 'Half Marathon', '18-35', '1', '1', '1', '01:11:30', '01:11:55', 4, 8),
(32, 'KOL002', 'Mitali Ghosh', 'F', 'Half Marathon', '18-35', '2', '1', '1', '01:15:45', '01:16:10', 4, 8),
(33, 'KOL003', 'Debashis Mukherjee', 'M', '10K Run', '36-50', '1', '1', '1', '00:37:20', '00:37:40', 4, 9),
(34, 'KOL004', 'Shreya Banerjee', 'F', '10K Run', '18-35', '2', '1', '1', '00:40:10', '00:40:35', 4, 9),
(35, 'KOL005', 'Arnab Chatterjee', 'M', 'Half Marathon', '36-50', '3', '2', '1', '01:17:25', '01:17:55', 4, 8),
(36, 'KOL006', 'Payel Sen', 'F', 'Half Marathon', '36-50', '4', '2', '1', '01:23:15', '01:23:45', 4, 8),
(37, 'KOL007', 'Bimal Roy', 'M', '10K Run', '51+', '3', '2', '1', '00:44:50', '00:45:15', 4, 9),
(38, 'KOL008', 'Rupa Bose', 'F', '10K Run', '36-50', '4', '2', '1', '00:46:30', '00:47:00', 4, 9),
(39, 'KOL009', 'Kaushik Dutta', 'M', 'Half Marathon', '18-35', '5', '3', '2', '01:20:40', '01:21:10', 4, 8),
(40, 'KOL010', 'Indrani Sarkar', 'F', 'Half Marathon', '51+', '6', '3', '1', '01:30:20', '01:30:55', 4, 8),

-- Chennai Beach Marathon Results (Event 5)
(41, 'CHN001', 'Karthik Subramanian', 'M', 'Half Marathon', '18-35', '1', '1', '1', '01:13:15', '01:13:40', 5, 10),
(42, 'CHN002', 'Priyanka Natarajan', 'F', 'Half Marathon', '18-35', '2', '1', '1', '01:16:50', '01:17:20', 5, 10),
(43, 'CHN003', 'Venkatesh Raman', 'M', '10K Run', '36-50', '1', '1', '1', '00:38:40', '00:39:00', 5, 11),
(44, 'CHN004', 'Lavanya Krishnamurthy', 'F', '10K Run', '18-35', '2', '1', '1', '00:41:25', '00:41:50', 5, 11),
(45, 'CHN005', 'Murugan Pillai', 'M', 'Half Marathon', '51+', '3', '2', '1', '01:27:30', '01:28:00', 5, 10),
(46, 'CHN006', 'Kamala Sundaram', 'F', 'Half Marathon', '36-50', '4', '2', '1', '01:24:10', '01:24:40', 5, 10),
(47, 'CHN007', 'Senthil Kumar', 'M', '10K Run', '18-35', '3', '2', '2', '00:42:15', '00:42:40', 5, 11),
(48, 'CHN008', 'Meenakshi Iyer', 'F', '10K Run', '51+', '4', '2', '1', '00:49:30', '00:50:00', 5, 11),
(49, 'CHN009', 'Balaji Naidu', 'M', 'Half Marathon', '36-50', '5', '3', '2', '01:22:45', '01:23:15', 5, 10),
(50, 'CHN010', 'Revathi Anand', 'F', 'Half Marathon', '18-35', '6', '3', '2', '01:26:30', '01:27:05', 5, 10);

-- Insert sample checkpoints for a few results (optional)
-- Example: 3 checkpoints for first result of each event
INSERT INTO checkpoint (id, checkpoint_number, time, result_id) VALUES

-- ========== MUMBAI MARATHON (Event 1) ==========
-- Full Marathon runners (42K) - 4 checkpoints each
-- Result 1 (MUM001)
(1, 1, '00:41:00', 1),    -- 10K
(2, 2, '01:23:30', 1),    -- 21K
(3, 3, '01:52:45', 1),    -- 30K
(4, 4, '02:20:15', 1),    -- 35K

-- Result 2 (MUM002)
(5, 1, '00:43:00', 2),
(6, 2, '01:26:15', 2),
(7, 3, '01:56:30', 2),
(8, 4, '02:24:45', 2),

-- Result 7 (MUM007)
(9, 1, '00:47:30', 7),
(10, 2, '01:35:20', 7),
(11, 3, '02:08:15', 7),
(12, 4, '02:40:30', 7),

-- Result 10 (MUM010)
(13, 1, '00:48:45', 10),
(14, 2, '01:38:20', 10),
(15, 3, '02:12:30', 10),
(16, 4, '02:45:15', 10),

-- Half Marathon runners (21K) - 2 checkpoints each
-- Result 3 (MUM003)
(17, 1, '00:22:30', 3),   -- 5K
(18, 2, '00:47:45', 3),   -- 10K

-- Result 4 (MUM004)
(19, 1, '00:23:45', 4),
(20, 2, '00:49:30', 4),

-- Result 8 (MUM008)
(21, 1, '00:26:15', 8),
(22, 2, '00:53:20', 8),

-- 10K runners - 1 checkpoint each
-- Result 5 (MUM005)
(23, 1, '00:18:45', 5),   -- 5K

-- Result 6 (MUM006)
(24, 1, '00:20:30', 6),

-- Result 9 (MUM009)
(25, 1, '00:22:10', 9),

-- ========== DELHI HALF MARATHON (Event 2) ==========
-- Half Marathon runners - 2 checkpoints each
-- Result 11 (DEL001)
(26, 1, '00:21:45', 11),
(27, 2, '00:45:30', 11),

-- Result 12 (DEL002)
(28, 1, '00:23:00', 12),
(29, 2, '00:47:45', 12),

-- Result 15 (DEL005)
(30, 1, '00:24:15', 15),
(31, 2, '00:49:20', 15),

-- Result 16 (DEL006)
(32, 1, '00:25:30', 16),
(33, 2, '00:52:15', 16),

-- Result 19 (DEL009)
(34, 1, '00:27:00', 19),
(35, 2, '00:56:30', 19),

-- Result 20 (DEL010)
(36, 1, '00:26:00', 20),
(37, 2, '00:53:45', 20),

-- 10K runners - 1 checkpoint each
-- Result 13 (DEL003)
(38, 1, '00:17:30', 13),

-- Result 14 (DEL004)
(39, 1, '00:19:00', 14),

-- Result 17 (DEL007)
(40, 1, '00:19:45', 17),

-- Result 18 (DEL008)
(41, 1, '00:23:30', 18),

-- ========== BANGALORE MIDNIGHT MARATHON (Event 3) ==========
-- Full Marathon runners - 4 checkpoints each
-- Result 21 (BLR001)
(42, 1, '00:42:00', 21),
(43, 2, '01:24:30', 21),
(44, 3, '01:54:15', 21),
(45, 4, '02:22:00', 21),

-- Result 22 (BLR002)
(46, 1, '00:44:30', 22),
(47, 2, '01:29:15', 22),
(48, 3, '02:00:30', 22),
(49, 4, '02:30:00', 22),

-- Result 25 (BLR005)
(50, 1, '00:46:00', 25),
(51, 2, '01:32:30', 25),
(52, 3, '02:05:45', 25),
(53, 4, '02:36:20', 25),

-- Result 28 (BLR008)
(54, 1, '00:50:00', 28),
(55, 2, '01:40:15', 28),
(56, 3, '02:15:30', 28),
(57, 4, '02:48:45', 28),

-- Half Marathon runners - 2 checkpoints each
-- Result 23 (BLR003)
(58, 1, '00:22:00', 23),
(59, 2, '00:46:30', 23),

-- Result 24 (BLR004)
(60, 1, '00:23:30', 24),
(61, 2, '00:48:15', 24),

-- Result 26 (BLR006)
(62, 1, '00:25:00', 26),
(63, 2, '00:51:30', 26),

-- Result 27 (BLR007)
(64, 1, '00:26:30', 27),
(65, 2, '00:54:00', 27),

-- Result 29 (BLR009)
(66, 1, '00:24:00', 29),
(67, 2, '00:50:00', 29),

-- Result 30 (BLR010)
(68, 1, '00:29:00', 30),
(69, 2, '01:00:30', 30),

-- ========== KOLKATA HERITAGE RUN (Event 4) ==========
-- Half Marathon runners - 2 checkpoints each
-- Result 31 (KOL001)
(70, 1, '00:21:00', 31),
(71, 2, '00:44:30', 31),

-- Result 32 (KOL002)
(72, 1, '00:22:45', 32),
(73, 2, '00:47:00', 32),

-- Result 35 (KOL005)
(74, 1, '00:23:30', 35),
(75, 2, '00:48:45', 35),

-- Result 36 (KOL006)
(76, 1, '00:25:45', 36),
(77, 2, '00:52:30', 36),

-- Result 39 (KOL009)
(78, 1, '00:24:30', 39),
(79, 2, '00:50:15', 39),

-- Result 40 (KOL010)
(80, 1, '00:28:00', 40),
(81, 2, '00:58:15', 40),

-- 10K runners - 1 checkpoint each
-- Result 33 (KOL003)
(82, 1, '00:18:00', 33),

-- Result 34 (KOL004)
(83, 1, '00:19:30', 34),

-- Result 37 (KOL007)
(84, 1, '00:21:45', 37),

-- Result 38 (KOL008)
(85, 1, '00:22:30', 38),

-- ========== CHENNAI BEACH MARATHON (Event 5) ==========
-- Half Marathon runners - 2 checkpoints each
-- Result 41 (CHN001)
(86, 1, '00:22:15', 41),
(87, 2, '00:46:00', 41),

-- Result 42 (CHN002)
(88, 1, '00:23:15', 42),
(89, 2, '00:48:00', 42),

-- Result 45 (CHN005)
(90, 1, '00:27:15', 45),
(91, 2, '00:56:00', 45),

-- Result 46 (CHN006)
(92, 1, '00:25:30', 46),
(93, 2, '00:53:00', 46),

-- Result 49 (CHN009)
(94, 1, '00:25:15', 49),
(95, 2, '00:51:45', 49),

-- Result 50 (CHN010)
(96, 1, '00:26:45', 50),
(97, 2, '00:54:15', 50),

-- 10K runners - 1 checkpoint each
-- Result 43 (CHN003)
(98, 1, '00:18:30', 43),

-- Result 44 (CHN004)
(99, 1, '00:20:00', 44),

-- Result 47 (CHN007)
(100, 1, '00:20:30', 47),

-- Result 48 (CHN008)
(101, 1, '00:24:00', 48);