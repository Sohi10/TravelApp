package com.travelapp.dao;

import com.travelapp.model.Booking;
import com.travelapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public void addBooking(Booking booking) {
        String sql = "INSERT INTO Booking (customer_id, flight_id, hotel_id, booking_date) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, booking.getCustomerId());
            if (booking.getFlightId() != null) {
                pstmt.setInt(2, booking.getFlightId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            if (booking.getHotelId() != null) {
                pstmt.setInt(3, booking.getHotelId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setDate(4, Date.valueOf(booking.getBookingDate()));
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    public List<Booking> searchBookings(Integer customerId, Integer flightId, Integer hotelId) {
        List<Booking> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT booking_id, customer_id, flight_id, hotel_id, booking_date FROM Booking WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (customerId != null) {
            sql.append(" AND customer_id = ?");
            params.add(customerId);
        }
        if (flightId != null) {
            sql.append(" AND flight_id = ?");
            params.add(flightId);
        }
        if (hotelId != null) {
            sql.append(" AND hotel_id = ?");
            params.add(hotelId);
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                }
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("customer_id"),
                        rs.getObject("flight_id") != null ? rs.getInt("flight_id") : null,
                        rs.getObject("hotel_id") != null ? rs.getInt("hotel_id") : null,
                        rs.getDate("booking_date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return bookings;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT booking_id, customer_id, flight_id, hotel_id, booking_date FROM Booking WHERE booking_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Booking booking = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                booking = new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("customer_id"),
                        rs.getObject("flight_id") != null ? rs.getInt("flight_id") : null,
                        rs.getObject("hotel_id") != null ? rs.getInt("hotel_id") : null,
                        rs.getDate("booking_date").toLocalDate()
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return booking;
    }

    // Update Booking
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE Booking SET customer_id = ?, flight_id = ?, hotel_id = ?, booking_date = ? WHERE booking_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, booking.getCustomerId());
            if (booking.getFlightId() != null) {
                pstmt.setInt(2, booking.getFlightId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            if (booking.getHotelId() != null) {
                pstmt.setInt(3, booking.getHotelId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setDate(4, Date.valueOf(booking.getBookingDate()));
            pstmt.setInt(5, booking.getBookingId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Delete Booking
    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM Booking WHERE booking_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }
}