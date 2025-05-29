-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 29, 2025 at 02:40 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `projet1`
--

-- --------------------------------------------------------

--
-- Table structure for table `authentification`
--

CREATE TABLE `authentification` (
  `email` varchar(100) NOT NULL,
  `firstname` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `gender` enum('Male','Female') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `authentification`
--

INSERT INTO `authentification` (`email`, `firstname`, `lastname`, `password`, `gender`, `created_at`) VALUES
('ahmed1@gmail.com', 'ahmed', 'lakouis', '123', 'Male', '2025-05-03 09:48:14'),
('ahmed@gmail.com', 'ilham', 'lakouis', '123', 'Male', '2025-05-03 09:41:41'),
('exemple@gmail.com', 'ilham', 'lakouis', '123', 'Female', '2025-04-29 07:33:42'),
('hibasoubai@gmail.com', 'hiba', 'soubai', 'hibasoubai', 'Female', '2025-04-29 08:19:40'),
('lakhtirisalma110@gmail.com', 'salma', 'lakhtiri', '123', 'Female', '2025-05-17 10:04:56'),
('lakhtrisalma@gmail.com', 'salma', 'lakhtiri', 'lakhtirisalma', 'Female', '2025-04-29 08:21:48'),
('salma@gmail.com', 'salma', 'salma', '123', 'Female', '2025-05-13 08:53:13'),
('salwa123@gmail.com', 'salwa', 'lakouis', '345', 'Female', '2025-05-13 11:29:46');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `authentification`
--
ALTER TABLE `authentification`
  ADD PRIMARY KEY (`email`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
