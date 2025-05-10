package com.travelapp.dao;

import com.travelapp.model.Flight;
import com.travelapp.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public void addFlight(Flight flight) {
        String sql = "INSERT INTO Flight (origin, destination, departure_time, arrival_time, price) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, flight.getOrigin());
            pstmt.setString(2, flight.getDestination());
            pstmt.setTimestamp(3, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setDouble(5, flight.getPrice());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        flight.setFlightId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding flight: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDateTime departureDate) {
        List<Flight> flights = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT flight_id, origin, destination, departure_time, arrival_time, price FROM Flight WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (origin != null && !origin.isEmpty()) {
            sql.append(" AND origin LIKE ?");
            params.add("%" + origin + "%");
        }
        if (destination != null && !destination.isEmpty()) {
            sql.append(" AND destination LIKE ?");
            params.add("%" + destination + "%");
        }
        if (departureDate != null) {
            sql.append(" AND DATE(departure_time) = ?");
            params.add(java.sql.Date.valueOf(departureDate.toLocalDate()));
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) param);
                }
                // Add other types if necessary
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                flights.add(new Flight(
                        rs.getInt("flight_id"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getTimestamp("departure_time").toLocalDateTime(),
                        rs.getTimestamp("arrival_time").toLocalDateTime(),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching flights: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return flights;
    }

    public Flight getFlightById(int flightId) {
        String sql = "SELECT flight_id, origin, destination, departure_time, arrival_time, price FROM Flight WHERE flight_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Flight flight = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, flightId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                flight = new Flight(
                        rs.getInt("flight_id"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getTimestamp("departure_time").toLocalDateTime(),
                        rs.getTimestamp("arrival_time").toLocalDateTime(),
                        rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching flight by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return flight;
    }

    // Update Flight
    public boolean updateFlight(Flight flight) {
        String sql = "UPDATE Flight SET origin = ?, destination = ?, departure_time = ?, arrival_time = ?, price = ? WHERE flight_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, flight.getOrigin());
            pstmt.setString(2, flight.getDestination());
            pstmt.setTimestamp(3, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setDouble(5, flight.getPrice());
            pstmt.setInt(6, flight.getFlightId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating flight: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    //  Delete Flight
    public boolean deleteFlight(int flightId) {
        String sql = "DELETE FROM Flight WHERE flight_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, flightId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting flight: " + e.getMessage());
            System.err.println("Note: Cannot delete flight if there are associated bookings (due to foreign key constraints).");
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    //  Get all flights
    public List<Flight> getAllFlights() {
        return searchFlights(null, null, null); // Calling search with nulls to get all
    }
}