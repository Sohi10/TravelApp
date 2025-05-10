package com.travelapp.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // SQL statements to drop tables 
    private static final String DROP_BOOKING_TABLE_SQL = "DROP TABLE IF EXISTS Booking";
    private static final String DROP_CUSTOMER_TABLE_SQL = "DROP TABLE IF EXISTS Customer";
    private static final String DROP_FLIGHT_TABLE_SQL = "DROP TABLE IF EXISTS Flight";
    private static final String DROP_HOTEL_TABLE_SQL = "DROP TABLE IF EXISTS Hotel";

    // SQL statements to create tables
    private static final String CREATE_CUSTOMER_TABLE_SQL =
            "CREATE TABLE Customer (" +
            "    customer_id INT PRIMARY KEY AUTO_INCREMENT," +
            "    name VARCHAR(100) NOT NULL," +
            "    email VARCHAR(100) NOT NULL," +
            "    phone VARCHAR(15) NOT NULL" +
            ")";

    private static final String CREATE_FLIGHT_TABLE_SQL =
            "CREATE TABLE Flight (" +
            "    flight_id INT PRIMARY KEY AUTO_INCREMENT," +
            "    origin VARCHAR(100) NOT NULL," +
            "    destination VARCHAR(100) NOT NULL," +
            "    departure_time DATETIME NOT NULL," +
            "    arrival_time DATETIME NOT NULL," +
            "    price DECIMAL(10,2) NOT NULL" +
            ")";

    private static final String CREATE_HOTEL_TABLE_SQL =
            "CREATE TABLE Hotel (" +
            "    hotel_id INT PRIMARY KEY AUTO_INCREMENT," +
            "    name VARCHAR(100) NOT NULL," +
            "    city VARCHAR(100) NOT NULL," +
            "    price_per_night DECIMAL(10,2) NOT NULL" +
            ")";

    private static final String CREATE_BOOKING_TABLE_SQL =
            "CREATE TABLE Booking (" +
            "    booking_id INT PRIMARY KEY AUTO_INCREMENT," +
            "    customer_id INT NOT NULL," +
            "    flight_id INT," +
            "    hotel_id INT," +
            "    booking_date DATE NOT NULL," +
            "    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)," +
            "    FOREIGN KEY (flight_id) REFERENCES Flight(flight_id)," +
            "    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)" +
            ")";

    // SQL statements for sample data
    private static final String INSERT_SAMPLE_CUSTOMER_SQL =
            "INSERT INTO Customer (name, email, phone) VALUES " +
            "('John Doe', 'john.doe@example.com', '123-456-7890')," +
            "('Jane Smith', 'jane.smith@example.com', '987-654-3210'),"+
            "('Alice Smith', 'alice@example.com', '1234567890'),"+
            "('Bob Johnson', 'bob@example.com', '2345678901'),"+
            "('Carol Lee', 'carol@example.com', '3456789012'),"+
            "('David Kim', 'david@example.com', '4567890123'),"+
            "('Eve Zhang', 'eve@example.com', '5678901234'),"+
            "('Frank White', 'frank@example.com', '6789012345'),"+
            "('Grace Brown', 'grace@example.com', '7890123456'),"+
            "('Henry Miller', 'henry@example.com', '8901234567'),"+
            "('Ivy Davis', 'ivy@example.com', '9012345678'),"+
            "('Jack Wilson', 'jack@example.com', '1123456789'),"+
            "('Karen Moore', 'karen@example.com', '2234567890'),"+
            "('Leo Taylor', 'leo@example.com', '3345678901'),"+
            "('Mia Anderson', 'mia@example.com', '4456789012'),"+
            "('Nick Thomas', 'nick@example.com', '5567890123'),"+
            "('Olivia Harris', 'olivia@example.com', '6678901234')";

    private static final String INSERT_SAMPLE_FLIGHT_SQL =
            "INSERT INTO Flight (origin, destination, departure_time, arrival_time, price) VALUES " +
            "('New York', 'Los Angeles', '2025-06-10 08:00:00', '2025-06-10 11:00:00', 250.00)," +
            "('Chicago', 'Miami', '2025-07-01 10:00:00', '2025-07-01 13:00:00', 180.50),"+
            "('San Francisco', 'Seattle', '2025-06-15 09:30:00', '2025-06-15 11:00:00', 130.00),"+
            "('Dallas', 'Denver', '2025-06-20 07:45:00', '2025-06-20 09:15:00', 120.75),"+
            "('Boston', 'Atlanta', '2025-07-05 06:00:00', '2025-07-05 08:30:00', 160.00),"+
            "('Houston', 'Orlando', '2025-07-10 13:15:00', '2025-07-10 16:00:00', 175.25),"+
            "('Philadelphia', 'Phoenix', '2025-07-12 11:00:00', '2025-07-12 14:30:00', 200.00),"+
            "('Las Vegas', 'Portland', '2025-07-15 15:00:00', '2025-07-15 17:15:00', 145.99),"+
            "('San Diego', 'Chicago', '2025-07-18 08:30:00', '2025-07-18 12:00:00', 190.00),"+
            "('Detroit', 'New York', '2025-07-20 17:45:00', '2025-07-20 19:15:00', 110.50),"+
            "('Baltimore', 'Charlotte', '2025-07-22 06:20:00', '2025-07-22 08:00:00', 95.75),"+
            "('Minneapolis', 'Las Vegas', '2025-07-25 12:00:00', '2025-07-25 14:45:00', 210.00),"+
            "('Nashville', 'Dallas', '2025-07-28 09:15:00', '2025-07-28 11:30:00', 140.00),"+
            "('Salt Lake City', 'San Francisco', '2025-07-30 14:00:00', '2025-07-30 16:00:00', 155.25),"+
            "('Cleveland', 'Boston', '2025-08-01 07:30:00', '2025-08-01 09:00:00', 125.00)";
            
    private static final String INSERT_SAMPLE_HOTEL_SQL =
            "INSERT INTO Hotel (name, city, price_per_night) VALUES " +
            "('Grand Hyatt', 'Los Angeles', 150.00)," +
            "('Hilton Downtown', 'Miami', 120.00),"+
            "('Marriott Marquis', 'New York', 210.50),"+
            "('Holiday Inn Express', 'Los Angeles', 98.75),"+
            "('The Ritz-Carlton', 'Chicago', 305.00),"+
            "('Hyatt Regency', 'San Francisco', 185.20),"+
            "('Best Western Plus', 'Austin', 89.99),"+
            "('Four Seasons', 'Las Vegas', 270.00),"+
            "('Radisson Blu', 'Atlanta', 150.50),"+
            "('Comfort Suites', 'Orlando', 110.00),"+
            "('Omni Hotel', 'Dallas', 142.30),"+
            "('The Westin', 'Seattle', 175.00),"+
            "('DoubleTree by Hilton', 'Denver', 130.25),"+
            "('Fairfield Inn', 'Phoenix', 95.00),"+
            "('Sheraton Grand', 'Boston', 195.00),"+
            "('La Quinta Inn', 'San Diego', 105.50)";
            
            

    private static final String INSERT_SAMPLE_BOOKING_SQL =
            "INSERT INTO Booking (customer_id, flight_id, hotel_id, booking_date) VALUES " +
            "(1, 1, NULL, '2025-05-01')," +
            "(2, NULL, 2, '2025-05-02'),"+
            "(3, 3, 3, '2025-05-03'),"+
            "(4, 4, NULL, '2025-05-04'),"+
            "(5, NULL, 5, '2025-05-05'),"+
            "(6, 6, 6, '2025-05-06'),"+
            "(7, NULL, 7, '2025-05-07'),"+
            "(8, 8, NULL, '2025-05-08'),"+
            "(9, 9, 9, '2025-05-09'),"+
            "(10, NULL, 10, '2025-05-10'),"+
            "(11, 11, NULL, '2025-05-11'),"+
            "(12, NULL, 12, '2025-05-12'),"+
            "(13, 13, 13, '2025-05-13'),"+
            "(14, 14, NULL, '2025-05-14'),"+
            "(15, NULL, 15, '2025-05-15')";


    public static void initializeDatabase() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();

            System.out.println("Attempting to initialize database and tables...");

            // Drop tables 
            System.out.println("Dropping existing tables (if any)...");
            statement.executeUpdate(DROP_BOOKING_TABLE_SQL);
            statement.executeUpdate(DROP_CUSTOMER_TABLE_SQL);
            statement.executeUpdate(DROP_FLIGHT_TABLE_SQL);
            statement.executeUpdate(DROP_HOTEL_TABLE_SQL);
            System.out.println("Existing tables dropped.");

            // Create tables
            System.out.println("Creating Customer table...");
            statement.executeUpdate(CREATE_CUSTOMER_TABLE_SQL);
            System.out.println("Customer table created.");

            System.out.println("Creating Flight table...");
            statement.executeUpdate(CREATE_FLIGHT_TABLE_SQL);
            System.out.println("Flight table created.");

            System.out.println("Creating Hotel table...");
            statement.executeUpdate(CREATE_HOTEL_TABLE_SQL);
            System.out.println("Hotel table created.");

            System.out.println("Creating Booking table...");
            statement.executeUpdate(CREATE_BOOKING_TABLE_SQL);
            System.out.println("Booking table created.");

            // Insert sample data
            System.out.println("Inserting sample data...");
            statement.executeUpdate(INSERT_SAMPLE_CUSTOMER_SQL);
            statement.executeUpdate(INSERT_SAMPLE_FLIGHT_SQL);
            statement.executeUpdate(INSERT_SAMPLE_HOTEL_SQL);
            statement.executeUpdate(INSERT_SAMPLE_BOOKING_SQL);
            System.out.println("Sample data inserted successfully.");

            System.out.println("Database initialization complete!");

        } catch (SQLException e) {
            System.err.println("ERROR: Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            
            throw new RuntimeException("Failed to initialize database.", e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
            DBConnection.closeConnection(connection);
        }
    }

}