-- Create the database
CREATE DATABASE IF NOT EXISTS travel_booking_db;

-- Use the database
USE travel_booking_db;

-- Create Customer Table
CREATE TABLE Customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL
);

-- Create Flight Table
CREATE TABLE Flight (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- Create Hotel Table
CREATE TABLE Hotel (
    hotel_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL
);

-- Create Booking Table
CREATE TABLE Booking (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    flight_id INT,
    hotel_id INT,
    booking_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (flight_id) REFERENCES Flight(flight_id),
    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)
);

-- Optional: Add some sample data for testing
INSERT INTO Customer (name, email, phone) VALUES
('John Doe', 'john.doe@example.com', '123-456-7890'),
('Jane Smith', 'jane.smith@example.com', '987-654-3210');

INSERT INTO Flight (origin, destination, departure_time, arrival_time, price) VALUES
('New York', 'Los Angeles', '2025-06-10 08:00:00', '2025-06-10 11:00:00', 250.00),
('Chicago', 'Miami', '2025-07-01 10:00:00', '2025-07-01 13:00:00', 180.50);

INSERT INTO Hotel (name, city, price_per_night) VALUES
('Grand Hyatt', 'Los Angeles', 150.00),
('Hilton Downtown', 'Miami', 120.00);

INSERT INTO Booking (customer_id, flight_id, hotel_id, booking_date) VALUES
(1, 1, NULL, '2025-05-01'),
(2, NULL, 2, '2025-05-02');